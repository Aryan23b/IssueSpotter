// Script to create an admin user in Supabase Auth and grant admin rights.
//
// Requires the SERVICE ROLE key (Supabase > Project Settings > API).
// Never put the service role key in a VITE_* variable or commit it: VITE_*
// values are bundled into the browser build.
//
// Usage:
//   SUPABASE_URL=... SUPABASE_SERVICE_ROLE_KEY=... \
//   ADMIN_EMAIL=you@example.com ADMIN_PASSWORD='a-strong-password' \
//   node scripts/create-admin-user.js
//
// Alternative without this script: create the user in the Supabase dashboard,
// then in the SQL editor run:
//   insert into public.admins (user_id)
//   select id from auth.users where email = 'you@example.com'
//   on conflict do nothing;

import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.SUPABASE_URL
const supabaseServiceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY
const adminEmail = process.env.ADMIN_EMAIL
const adminPassword = process.env.ADMIN_PASSWORD

if (!supabaseUrl || !supabaseServiceRoleKey || !adminEmail || !adminPassword) {
  console.error('Set SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY, ADMIN_EMAIL and ADMIN_PASSWORD.')
  process.exit(1)
}

if (adminPassword.length < 12) {
  console.error('ADMIN_PASSWORD must be at least 12 characters.')
  process.exit(1)
}

const supabase = createClient(supabaseUrl, supabaseServiceRoleKey, {
  auth: {
    autoRefreshToken: false,
    persistSession: false
  }
})

async function createAdminUser() {
  try {
    const { data: authUser, error: authError } = await supabase.auth.admin.createUser({
      email: adminEmail,
      password: adminPassword,
      email_confirm: true // Auto-confirm email
    })

    if (authError) {
      console.error('Error creating auth user:', authError)
      return
    }

    console.log('Auth user created:', authUser.user.id)

    // Grant admin rights (table created by sql/001_admin_rls.sql)
    const { error: adminError } = await supabase
      .from('admins')
      .insert({ user_id: authUser.user.id })

    if (adminError) {
      console.error('Error granting admin rights:', adminError)
      return
    }

    console.log('Admin user created successfully!')
    console.log('User ID:', authUser.user.id)
    console.log('Email:', authUser.user.email)

  } catch (error) {
    console.error('Unexpected error:', error)
  }
}

createAdminUser()
