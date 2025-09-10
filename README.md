🏙 IssueSpotter

IssueSpotter is a modern Android application that empowers citizens to report and track local community issues like potholes, broken streetlights, cleanliness problems, and more.
Built with Jetpack Compose and Supabase, it provides a seamless and engaging platform for citizens to contribute to their community's improvement.

📑 Table of Contents

Features

Demo

Installation

Usage

Tech Stack

Architecture

Challenges & Solutions

Future Improvements

Contributing

Credits

Contact

✨ Features

🎨 Modern UI – Built with Jetpack Compose + Material Design 3

⚡ Real-time Reporting – Instant issue submission with image upload

📍 Location Services – Google Maps integration for precise issue location

👍 Community Engagement – Upvote system for important issues

🔐 Secure Authentication – Email/password authentication with Supabase

🌙 Dark/Light Mode – Full theme support with dynamic switching

📊 Status Tracking – Track issue progress from Open → Resolved

🖼 Image Support – Upload photos for better issue context


🚀 Live Demo https://youtu.be/23NNdrc1SDA


## 📸 Screenshots  

<p align="center">
  <img src="https://github.com/user-attachments/assets/4c3b3fb9-b48b-4807-9099-b07aca8348ca" width="250" />
  <img src="https://github.com/user-attachments/assets/7d1cee41-db9e-4eeb-8e23-18f2b490da8e" width="250" />
  <img src="https://github.com/user-attachments/assets/1285c312-0955-41ee-81be-8113ae6b41dc" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/cab4722e-562b-41f2-a087-00ddc3a70f0d" width="250" />
  <img src="https://github.com/user-attachments/assets/54740687-fc2c-4b36-8e1b-6e1179f97241" width="250" />
  <img src="https://github.com/user-attachments/assets/f19d9232-8934-4800-b171-1c8d223cfd4f" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/1269ac6d-761b-4124-8ec7-3380b938b576" width="250" />
  <img src="https://github.com/user-attachments/assets/0aee7935-5468-4b63-90dd-1e7fb557ef00" width="250" />
  <img src="https://github.com/user-attachments/assets/ff2dd3c4-5265-4f7c-8101-8d3683123b84" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/b82b919d-c0bf-49d7-b743-2c55c7cdd70d" width="250" />
  <img src="https://github.com/user-attachments/assets/6ea913b8-4203-478f-8cca-e49ab921f467" width="250" />
  <img src="https://github.com/user-attachments/assets/f5e0ec06-f55b-432f-93a1-1a271b638a8a" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/96d55013-28b9-4d91-8be4-03efbcd69ce3" width="250" />
  <img src="https://github.com/user-attachments/assets/4e9262e4-2673-42dd-b24e-de3c0e8b5077" width="250" />
  <img src="https://github.com/user-attachments/assets/795c4eab-e594-443a-a574-30444dc118b1" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/364d56d6-181f-4be6-ae3f-9afdbad5bc87" width="250" />
  <img src="https://github.com/user-attachments/assets/ce932b55-bffa-4bcc-a112-0143accbb2f7" width="250" />
</p>


	
	
⚙ Installation

Clone the repository:

git clone https://github.com/your-username/IssueSpotter.git
cd IssueSpotter


Open in Android Studio

Configure Supabase & Google Maps

Add your Supabase project URL & API keys in local.properties

Set up Google Maps API key in AndroidManifest.xml

Build & Run the app

🚀 Usage

🔑 Register/Login – Secure authentication with email/password

📝 Report Issues – Add title, description, category & location

📸 Upload Photos – Attach images for better issue visibility

📍 Track Progress – Monitor issue status updates (Open → In Progress → Resolved)

👍 Community Interaction – Upvote important issues in your locality

🛠 Tech Stack

Frontend (Mobile)

⚛ Kotlin – Primary programming language

🎨 Jetpack Compose – Modern declarative UI toolkit

🧩 Material Design 3 – Latest Material components

🔄 Android Architecture Components – ViewModel, LiveData, Navigation

Backend & Database

🔧 Supabase – Open-source Firebase alternative

🗄 PostgreSQL – Relational database with RLS

🔗 PostgREST – Auto-generated REST API

💾 Supabase Storage – File storage for issue images

APIs & Services

📍 Google Maps SDK – Location services & map integration

🌐 Ktor Client – Network operations

🖼 Coil – Image loading in Compose

Development Tools

🖥 Android Studio – Official IDE

⚡ Gradle – Build automation

🌀 Git – Version control

🏗 Architecture
Presentation Layer (Compose)
 ├── UI Components
 ├── Navigation
 ├── ViewModels
 └── State Management

Data Layer
 ├── Supabase Client
 ├── Location Services
 ├── Image Processing
 └── Local Storage

Infrastructure
 ├── Supabase Backend
 ├── PostgreSQL Database
 ├── Supabase Storage
 └── Google Maps API

🧩 Challenges & Solutions

Challenge 1: Real-time Upvote Synchronization

📌 Problem: Ensuring upvote counts remain consistent across all users

✅ Solution: Implemented Supabase RLS with atomic PostgreSQL function updates

Challenge 2: Location Services Integration

📌 Problem: Handling location permissions and precise coordinates

✅ Solution: Integrated Google Maps Compose with fallback mechanisms

Challenge 3: Image Upload & Storage

📌 Problem: Efficiently handling large images

✅ Solution: Added compression + caching with Supabase Storage

Challenge 4: Offline Functionality

📌 Problem: Poor user experience during no connectivity

✅ Solution: Implemented error handling + retry mechanisms

🚀 Future Improvements

🔔 Push notifications for issue updates

📂 Offline data caching for usage without internet

💬 Social features (comments & discussions)

🤖 AI-based automatic issue categorization

🌍 Multi-language support

🛠 Admin dashboard for municipal authorities

📊 Analytics dashboard for trends & statistics

🤝 Contributing

We welcome contributions! 🎉

Fork the repo

Create a feature branch

git checkout -b feature/amazing-feature


Commit changes

git commit -m "Add amazing feature"


Push & open a Pull Request 🚀

📜 Credits

⚡ Supabase – Open-source Firebase alternative

💻 JetBrains – Kotlin & development tools

🗺 Google – Android Jetpack Compose & Maps SDK

🎨 Material Design – UI design system

🙏 Special thanks to the open-source community ❤

📬 Contact

👨‍💻 Author: Aryan Baranwal,
            Mohit Anuragi,
            Purvil Patel
            
📧 Email: baranwalaryan23@gmail.com,
          anuragimohit468@gmail.com,
          patelpurvil3105@gmail.com.

🐙 GitHub: [aryan23b](https://github.com/aryan23b) • [mohitAnuragi](https://github.com/mohitAnuragi) • [purvil3105](https://github.com/purvil3105) |
           

💼 LinkedIn: [Aryan Baranwal](https://www.linkedin.com/in/aryan-baranwal-75b9b129a?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app)•
 [Mohit A.](https://www.linkedin.com/in/mohit-a-52989b2b2) • [Purvil Patel](https://www.linkedin.com/in/purvil-patel-8201972a1) |

⭐ If you found this project helpful, please give it a star on GitHub! ⭐
