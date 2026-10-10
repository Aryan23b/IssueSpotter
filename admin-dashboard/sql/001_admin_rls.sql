-- =============================================
-- 001_admin_rls.sql
-- Server-side admin authorization for IssueSpotter.
--
-- Run once in the Supabase SQL editor. Safe to re-run.
--
-- After running, promote your admin account(s):
--   insert into public.admins (user_id)
--   select id from auth.users where email = 'you@example.com'
--   on conflict do nothing;
--
-- Model:
--   * Citizens (any signed-in user) can read reports, create their own
--     reports (status 'pending'), and upvote each report once.
--   * Only users listed in public.admins can update or delete reports.
--   * upvote_count is maintained by a trigger, never by clients.
-- =============================================

begin;

-- ---------------------------------------------
-- 1. Admins table + is_admin() helper
-- ---------------------------------------------
create table if not exists public.admins (
  user_id    uuid primary key references auth.users(id) on delete cascade,
  created_at timestamptz not null default now()
);

alter table public.admins enable row level security;

-- No insert/update/delete policies: admins are managed only from the
-- SQL editor (service role), never from the app or dashboard.
drop policy if exists "admins read self" on public.admins;
create policy "admins read self" on public.admins
  for select to authenticated
  using (user_id = auth.uid());

create or replace function public.is_admin()
returns boolean
language sql
stable
security definer
set search_path = public
as $$
  select exists (select 1 from public.admins where user_id = auth.uid())
$$;

revoke all on function public.is_admin() from public;
grant execute on function public.is_admin() to authenticated;

-- ---------------------------------------------
-- 2. Drop any existing policies on the tables we manage, so no older
--    permissive policy (policies are OR'ed) can bypass the new rules.
-- ---------------------------------------------
do $$
declare
  p record;
begin
  for p in
    select policyname, tablename
    from pg_policies
    where schemaname = 'public'
      and tablename in ('reports', 'report_upvotes', 'cleanup_logs', 'issues', 'reports_data')
  loop
    execute format('drop policy %I on public.%I', p.policyname, p.tablename);
  end loop;
end $$;

-- ---------------------------------------------
-- 3. report_upvotes integrity: one upvote per user per report
-- ---------------------------------------------
delete from public.report_upvotes
where report_id is null or user_id is null;

delete from public.report_upvotes a
using public.report_upvotes b
where a.report_id = b.report_id
  and a.user_id = b.user_id
  and a.id > b.id;

alter table public.report_upvotes alter column report_id set not null;
alter table public.report_upvotes alter column user_id set not null;

-- Deleting a report removes its upvotes instead of failing on the FK
alter table public.report_upvotes drop constraint if exists report_upvotes_report_id_fkey;
alter table public.report_upvotes
  add constraint report_upvotes_report_id_fkey
  foreign key (report_id) references public.reports(id) on delete cascade;

alter table public.report_upvotes drop constraint if exists report_upvotes_unique;
alter table public.report_upvotes
  add constraint report_upvotes_unique unique (report_id, user_id);

-- ---------------------------------------------
-- 4. Atomic upvote_count maintained by trigger
-- ---------------------------------------------
create or replace function public.sync_report_upvote_count()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  if tg_op = 'INSERT' then
    update public.reports
    set upvote_count = coalesce(upvote_count, 0) + 1
    where id = new.report_id;
    return new;
  elsif tg_op = 'DELETE' then
    update public.reports
    set upvote_count = greatest(coalesce(upvote_count, 0) - 1, 0)
    where id = old.report_id;
    return old;
  end if;
  return null;
end;
$$;

drop trigger if exists report_upvotes_count on public.report_upvotes;
create trigger report_upvotes_count
  after insert or delete on public.report_upvotes
  for each row execute function public.sync_report_upvote_count();

-- Recompute existing counts once from the source of truth
update public.reports r
set upvote_count = coalesce(u.cnt, 0)
from (
  select r2.id, count(u2.id) as cnt
  from public.reports r2
  left join public.report_upvotes u2 on u2.report_id = r2.id
  group by r2.id
) u
where u.id = r.id;

-- ---------------------------------------------
-- 5. RLS: reports
-- ---------------------------------------------
alter table public.reports enable row level security;

create policy "reports readable by signed-in users" on public.reports
  for select to authenticated
  using (true);

create policy "users create own pending reports" on public.reports
  for insert to authenticated
  with check (user_id = auth.uid() and status = 'pending');

create policy "admins update reports" on public.reports
  for update to authenticated
  using (public.is_admin())
  with check (public.is_admin());

create policy "admins delete reports" on public.reports
  for delete to authenticated
  using (public.is_admin());

-- ---------------------------------------------
-- 6. RLS: report_upvotes
-- ---------------------------------------------
alter table public.report_upvotes enable row level security;

create policy "upvotes readable by signed-in users" on public.report_upvotes
  for select to authenticated
  using (true);

create policy "users upvote as themselves" on public.report_upvotes
  for insert to authenticated
  with check (user_id = auth.uid());

create policy "users remove own upvote" on public.report_upvotes
  for delete to authenticated
  using (user_id = auth.uid());

-- ---------------------------------------------
-- 7. cleanup_logs (used by the dashboard's report cleanup service)
-- ---------------------------------------------
create table if not exists public.cleanup_logs (
  id                      serial primary key,
  cleanup_date            timestamptz not null default now(),
  reports_deleted         integer not null default 0,
  cutoff_date             timestamptz not null,
  deleted_report_ids      text[] default '{}',
  deleted_reports_summary jsonb default '[]',
  created_at              timestamptz not null default now()
);

-- Report ids are UUIDs; the original script declared INTEGER[], which made
-- every log insert fail.
alter table public.cleanup_logs
  alter column deleted_report_ids type text[] using deleted_report_ids::text[];

alter table public.cleanup_logs enable row level security;

create policy "admins read cleanup logs" on public.cleanup_logs
  for select to authenticated
  using (public.is_admin());

create policy "admins write cleanup logs" on public.cleanup_logs
  for insert to authenticated
  with check (public.is_admin());

create or replace function public.create_cleanup_logs_table()
returns void
language sql
as $$
  select 1;
$$;

-- ---------------------------------------------
-- 8. Legacy tables: lock down (RLS on, no policies = no client access)
-- ---------------------------------------------
alter table public.issues enable row level security;
alter table public.reports_data enable row level security;

-- ---------------------------------------------
-- 9. Storage bucket 'reports' (report photos)
--    App uploads to: reports/<user_id>/<uuid>.jpg
-- ---------------------------------------------
drop policy if exists "report photos public read" on storage.objects;
create policy "report photos public read" on storage.objects
  for select
  using (bucket_id = 'reports');

drop policy if exists "users upload own report photos" on storage.objects;
create policy "users upload own report photos" on storage.objects
  for insert to authenticated
  with check (
    bucket_id = 'reports'
    and (storage.foldername(name))[1] = 'reports'
    and (storage.foldername(name))[2] = auth.uid()::text
  );

drop policy if exists "admins delete report photos" on storage.objects;
create policy "admins delete report photos" on storage.objects
  for delete to authenticated
  using (bucket_id = 'reports' and public.is_admin());

commit;

-- ---------------------------------------------
-- Check: storage.objects may still carry older permissive policies for
-- the 'reports' bucket. List them and drop any not created above:
--   select policyname, cmd, qual, with_check
--   from pg_policies
--   where schemaname = 'storage' and tablename = 'objects';
-- ---------------------------------------------
