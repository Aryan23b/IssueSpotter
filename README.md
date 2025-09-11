# 🏙 IssueSpotter  

[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://developer.android.com/)  
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue?logo=kotlin)](https://kotlinlang.org/)  
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-purple?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)  
[![Supabase](https://img.shields.io/badge/Backend-Supabase-teal?logo=supabase)](https://supabase.com/)  
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)  

**IssueSpotter** is a modern Android application that empowers citizens to **report and track community issues** such as potholes, broken streetlights, cleanliness problems, and more.  
With **Jetpack Compose UI** and **Supabase backend**, it delivers a **seamless, real-time experience** for civic engagement.  

---

## 📑 Table of Contents  

- [✨ Features](#-features)  
- [📺 Demo](#-demo)  
- [📸 Screenshots](#-screenshots)  
- [⚙ Installation](#-installation)  
- [🚀 Usage](#-usage)  
- [🛠 Tech Stack](#-tech-stack)  
- [🏗 Architecture](#-architecture)  
- [🧩 Challenges & Solutions](#-challenges--solutions)  
- [🚀 Future Improvements](#-future-improvements)  
- [🤝 Contributing](#-contributing)  
- [📜 Credits](#-credits)  
- [📬 Contact](#-contact)  

---

## ✨ Features  

- 🎨 **Modern UI** – Jetpack Compose + Material Design 3  
- ⚡ **Real-time Reporting** – Instant submission with image upload  
- 📍 **Location Services** – Google Maps integration for precision  
- 👍 **Community Engagement** – Upvote system for priority issues  
- 🔐 **Secure Authentication** – Email/password with Supabase  
- 🌙 **Dark/Light Mode** – Full theme support  
- 📊 **Status Tracking** – Open → In Progress → Resolved  
- 🖼 **Image Support** – Attach photos for better context  

---

## 📺 Demo  

🎥 [**Watch Live Demo**](https://youtu.be/23NNdrc1SDA)  

---

## 📸 Screenshots  

<p align="center">
  <img src="https://github.com/user-attachments/assets/7d1cee41-db9e-4eeb-8e23-18f2b490da8e" width="250" />
  <img src="https://github.com/user-attachments/assets/1285c312-0955-41ee-81be-8113ae6b41dc" width="250" />
  <img src="https://github.com/user-attachments/assets/cab4722e-562b-41f2-a087-00ddc3a70f0d" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/f19d9232-8934-4800-b171-1c8d223cfd4f" width="250" />
  <img src="https://github.com/user-attachments/assets/1269ac6d-761b-4124-8ec7-3380b938b576" width="250" />
  <img src="https://github.com/user-attachments/assets/ff2dd3c4-5265-4f7c-8101-8d3683123b84" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/6ea913b8-4203-478f-8cca-e49ab921f467" width="250" />
  <img src="https://github.com/user-attachments/assets/f5e0ec06-f55b-432f-93a1-1a271b638a8a" width="250" />
  <img src="https://github.com/user-attachments/assets/ce932b55-bffa-4bcc-a112-0143accbb2f7" width="250" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/96d55013-28b9-4d91-8be4-03efbcd69ce3" width="250" />
  <img src="https://github.com/user-attachments/assets/795c4eab-e594-443a-a574-30444dc118b1" width="250" />
</p>

---

## ⚙ Installation  

```bash
# Clone the repository
git clone [https://github.com/Aryan23b/IssueSpotter].git
cd IssueSpotter


🚀 Usage

🔑 Register/Login – Secure Supabase authentication

📝 Report Issues – Add title, description, category & location

📸 Upload Photos – Attach images for better visibility

📍 Track Progress – Monitor issue status updates

👍 Community Interaction – Upvote important issues

🛠 Tech Stack

Frontend (Mobile)

⚛ Kotlin – Primary language

🎨 Jetpack Compose – Declarative UI

🧩 Material Design 3

🔄 ViewModel, LiveData, Navigation

Backend & Database

🔧 Supabase – Firebase alternative

🗄 PostgreSQL + RLS – Relational database

🔗 PostgREST – Auto REST API

💾 Supabase Storage – Image uploads

APIs & Services

📍 Google Maps SDK – Location services

🌐 Ktor Client – Networking

🖼 Coil – Image loading

Development Tools

🖥 Android Studio

⚡ Gradle

🌀 Git

🏗 Architecture

Presentation Layer (Jetpack Compose)
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

Real-time Upvote Synchronization

📌 Problem: Keeping votes consistent

✅ Solution: Supabase RLS + PostgreSQL atomic updates

Location Services

📌 Problem: Handling permissions & precision

✅ Solution: Google Maps Compose + fallback

Image Upload & Storage

📌 Problem: Large image handling

✅ Solution: Compression + caching

Offline Functionality

📌 Problem: Bad UX without internet

✅ Solution: Retry + graceful error handling

🚀 Future Improvements

🔔 Push notifications for updates

📂 Offline data caching

💬 Social features (comments & discussions)

🤖 AI-based automatic categorization

🌍 Multi-language support

🛠 Admin dashboard for municipal authorities

📊 Analytics dashboard for trends

🤝 Contributing

We welcome contributions! 🎉

Fork the repo

Create a feature branch

git checkout -b feature/amazing-feature


Commit changes

git commit -m "Add amazing feature"


Push & open a PR 🚀

📜 Credits

⚡ Supabase – Open-source Firebase alternative

💻 JetBrains – Kotlin & dev tools

🗺 Google – Jetpack Compose & Maps SDK

🎨 Material Design – Design system

🙏 Special thanks to the open-source community ❤

## 📬 Contact  

📧 **Emails**  
- [baranwalaryan23@gmail.com](mailto:baranwalaryan23@gmail.com)  
- [anuragimohit468@gmail.com](mailto:anuragimohit468@gmail.com)  
- [patelpurvil3105@gmail.com](mailto:patelpurvil3105@gmail.com)  

👨‍💻 **Authors**  
- [Aryan Baranwal](https://www.linkedin.com/in/aryan-baranwal-75b9b129a)  
- [Mohit Anuragi](https://www.linkedin.com/in/mohit-a-52989b2b2)  
- [Purvil Patel](https://www.linkedin.com/in/purvil-patel-8201972a1)  

### 🔗 GitHub Profiles  
[![Aryan](https://img.shields.io/badge/GitHub-aryan23b-black?logo=github)](https://github.com/aryan23b)  
[![Mohit](https://img.shields.io/badge/GitHub-mohitAnuragi-black?logo=github)](https://github.com/mohitAnuragi)  
[![Purvil](https://img.shields.io/badge/GitHub-purvil3105-black?logo=github)](https://github.com/purvil3105)  
 


⭐ If you found this project helpful, don’t forget to star the repo on GitHub! ⭐
