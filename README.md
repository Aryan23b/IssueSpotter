# IssueSpotter

IssueSpotter is a civic issue reporting platform. Citizens use the Android app to report local problems such as potholes, broken streetlights and water logging, with a photo and GPS location. Municipal staff use the web-based admin dashboard to review reports and update their status.

Demo video: https://youtu.be/23NNdrc1SDA

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/1285c312-0955-41ee-81be-8113ae6b41dc" width="220" alt="Home feed" />
  <img src="https://github.com/user-attachments/assets/1269ac6d-761b-4124-8ec7-3380b938b576" width="220" alt="Report a new issue" />
  <img src="https://github.com/user-attachments/assets/ff2dd3c4-5265-4f7c-8101-8d3683123b84" width="220" alt="Issue details" />
  <img src="https://github.com/user-attachments/assets/f19d9232-8934-4800-b171-1c8d223cfd4f" width="220" alt="Profile" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f5e0ec06-f55b-432f-93a1-1a271b638a8a" width="880" alt="Admin dashboard - reports" />
</p>

<p align="center"><em>Home feed, new report, issue details, profile, and the admin dashboard</em></p>

## Features

**Android app**
- Email and password sign-up and login
- Report an issue with title, description, category, photo and current location
- Browse all reports and filter by category
- Issue details with photo, map location and status
- Upvote issues (one vote per user) to signal priority
- Profile with contributor rating and a list of your own reports
- Light and dark theme

**Admin dashboard**
- Admin-only access, enforced by the database
- Reports table with search, status and category filters
- Report detail view with photo, map and status updates
- Analytics overview
- Automatic cleanup of resolved reports after 15 days, with an audit log

## Tech Stack

| Area | Technologies |
|---|---|
| Android app | Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModel, Coil, Google Maps Compose |
| Admin dashboard | React, Vite, Material UI, Leaflet |
| Backend | Supabase: PostgreSQL, Auth, Storage, Row Level Security |

## Project Structure

```
IssueSpotter/
├── app/                      Android app (Kotlin, Jetpack Compose)
└── admin-dashboard/          Admin web dashboard (React, Vite)
    └── sql/                  Database schema and security migration
```

## Security Model

Access rules are enforced in PostgreSQL with Row Level Security, not in the clients:

- Any signed-in user can read reports, create their own reports and upvote each report once.
- Only users listed in the `admins` table can change a report's status or delete reports.
- Upvote counts are maintained by a database trigger, so concurrent votes are never lost.

See `admin-dashboard/sql/001_admin_rls.sql` for the full policy set.

## Getting Started

### Prerequisites

- Android Studio with JDK 17 or 21 selected as the Gradle JDK
- Node.js 18 or later
- A Supabase project

### 1. Database

1. In the Supabase SQL editor, run `admin-dashboard/sql/001_admin_rls.sql`.
2. Create a storage bucket named `reports` and make it public.
3. Create your admin account in Supabase Auth, then grant it admin rights:

   ```sql
   insert into public.admins (user_id)
   select id from auth.users where email = 'you@example.com';
   ```

### 2. Android app

1. Clone the repository and open the project root in Android Studio.

   ```bash
   git clone https://github.com/MohitAnuragi/IssueSpotter.git
   ```

2. Set your Supabase URL and anon key in `app/src/main/java/com/example/issuespotter/clients/SupabaseClient.kt`.
3. Set your Google Maps API key in `app/src/main/AndroidManifest.xml`.
4. Sync Gradle and run the `app` configuration on a device or emulator (Android 7.0, API 24 or later).

### 3. Admin dashboard

```bash
cd admin-dashboard
cp .env.example .env      # then fill in your Supabase and Google Maps values
npm install
npm run dev
```

The dashboard runs at `http://localhost:5173`. Sign in with the account you added to the `admins` table.

## Authors

- Aryan Baranwal: [GitHub](https://github.com/aryan23b), [LinkedIn](https://www.linkedin.com/in/aryan-baranwal-75b9b129a)
- Mohit Anuragi: [GitHub](https://github.com/mohitAnuragi), [LinkedIn](https://www.linkedin.com/in/mohit-a-52989b2b2)
- Purvil Patel: [GitHub](https://github.com/purvil3105), [LinkedIn](https://www.linkedin.com/in/purvil-patel-8201972a1)
