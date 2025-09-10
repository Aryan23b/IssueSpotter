IssueSpotter🏙️
[https://via.placeholder.com/800x400/3F51B5/FFFFFF?text=Civic+Track+-+Community+Issue+Reporting+App](https://issuespotter.vercel.app/)

Description
IssueSpotter is a modern Android application that empowers citizens to report and track local community issues like potholes, broken streetlights, cleanliness problems, and more. Built with Jetpack Compose and Supabase, it provides a seamless platform for citizens to contribute to their community's improvement.

Table of Contents:

Features
Demo
Installation
Usage
Tech Stack
Architecture
Challenges & Solutions
Future Improvements
Credits

Features
 Modern UI: Built with Jetpack Compose and Material Design 3

 Real-time Reporting: Instant issue submission with image upload

 Location Services: Google Maps integration for precise issue location

 Community Engagement: Upvote system for important issues

Secure Authentication: Email/password auth with Supabase

Dark/Light Theme: Full theme support with dynamic switching

Status Tracking: Track issue progress from Open to Resolved

Image Support: Upload photos of issues for better context

Demo
Live Demo
Download APK

Screenshots:
![WhatsApp Image 2025-09-10 at 19 31 08_3005d1dc](https://github.com/user-attachments/assets/4c3b3fb9-b48b-4807-9099-b07aca8348ca)
![WhatsApp Image 2025-09-10 at 19 31 09_f3a6ed3e](https://github.com/user-attachments/assets/7d1cee41-db9e-4eeb-8e23-18f2b490da8e)
![WhatsApp Image 2025-09-10 at 19 31 09_903c06bc](https://github.com/user-attachments/assets/1285c312-0955-41ee-81be-8113ae6b41dc)
![WhatsApp Image 2025-09-10 at 19 31 10_0dee1847](https://github.com/user-attachments/assets/cab4722e-562b-41f2-a087-00ddc3a70f0d)
![WhatsApp Image 2025-09-10 at 19 31 10_44109be3](https://github.com/user-attachments/assets/54740687-fc2c-4b36-8e1b-6e1179f97241)
![WhatsApp Image 2025-09-10 at 19 31 10_55e981e9](https://github.com/user-attachments/assets/f19d9232-8934-4800-b171-1c8d223cfd4f)
![WhatsApp Image 2025-09-10 at 19 31 11_7420a9cd](https://github.com/user-attachments/assets/1269ac6d-761b-4124-8ec7-3380b938b576)
![WhatsApp Image 2025-09-10 at 19 31 11_d1976f09](https://github.com/user-attachments/assets/0aee7935-5468-4b63-90dd-1e7fb557ef00)
![WhatsApp Image 2025-09-10 at 19 31 12_41b1e1bf](https://github.com/user-attachments/assets/ff2dd3c4-5265-4f7c-8101-8d3683123b84)
![WhatsApp Image 2025-09-10 at 19 37 26_bea93e42](https://github.com/user-attachments/assets/b82b919d-c0bf-49d7-b743-2c55c7cdd70d)
![WhatsApp Image 2025-09-10 at 19 37 26_de3dfea9](https://github.com/user-attachments/assets/6ea913b8-4203-478f-8cca-e49ab921f467)
![WhatsApp Image 2025-09-10 at 19 37 26_3b80abe2](https://github.com/user-attachments/assets/f5e0ec06-f55b-432f-93a1-1a271b638a8a)
![WhatsApp Image 2025-09-10 at 19 37 27_3e38b0fd](https://github.com/user-attachments/assets/96d55013-28b9-4d91-8be4-03efbcd69ce3)
![WhatsApp Image 2025-09-10 at 19 37 28_fc18c3b0](https://github.com/user-attachments/assets/4e9262e4-2673-42dd-b24e-de3c0e8b5077)
![WhatsApp Image 2025-09-10 at 19 37 28_c557407f](https://github.com/user-attachments/assets/795c4eab-e594-443a-a574-30444dc118b1)
![WhatsApp Image 2025-09-10 at 19 37 29_6586072f](https://github.com/user-attachments/assets/364d56d6-181f-4be6-ae3f-9afdbad5bc87)
![WhatsApp Image 2025-09-10 at 19 37 29_fbe7a47c](https://github.com/user-attachments/assets/ce932b55-bffa-4bcc-a112-0143accbb2f7)
















Login Screen	Report Issues	View Issues

Registration & Login: Create an account or login with email/password

Report Issues: Click "Report Issue" to submit new community problems

Add Details: Provide title, description, category, and location

Upload Photos: Attach images for better issue context

Track Progress: View your reported issues and their status

Community Interaction: Upvote important issues in your area

Tech Stack
Frontend
Kotlin: Primary programming language

Jetpack Compose: Modern declarative UI toolkit

Material Design 3: Latest material design components

Android Architecture Components: ViewModel, LiveData, Navigation

Backend & Database
Supabase: Open-source Firebase alternative

PostgreSQL: Relational database with Row Level Security

PostgREST: Auto-generated REST API from database

Supabase Storage: File storage for issue images

APIs & Services
Google Maps SDK: Location services and maps integration

Ktor Client: HTTP client for network operations

Coil: Image loading library for Compose

Development Tools
Android Studio: Official IDE for Android development

Gradle: Build automation and dependency management

Git: Version control system

Architecture
text
 Presentation Layer (Compose)
├── UI Components
├──  Navigation
├──  ViewModels
└── State Management

 Data Layer
├── Supabase Client
├──  Location Services
├── Image Processing
└── Local Storage

 Infrastructure
├──  Supabase Backend
├──  PostgreSQL Database
├──  Supabase Storage
└──  Google Maps API

Challenges & Solutions
Challenge 1: Real-time Upvote Synchronization
Problem: Ensuring upvote counts are consistent across all users in real-time
Solution: Implemented Supabase Row Level Security with atomic updates using PostgreSQL functions

Challenge 2: Location Services Integration
Problem: Handling location permissions and precise coordinates
Solution: Used Google Maps Compose library with proper permission handling and fallback mechanisms

Challenge 3: Image Upload and Storage
Problem: Efficiently handling image uploads and storage management
Solution: Implemented Supabase Storage with optimized image compression and caching

Challenge 4: Offline Functionality
Problem: Handling network connectivity issues gracefully
Solution: Implemented robust error handling and retry mechanisms with user feedback

Future Improvements
Push Notifications: Alert users when their issues are updated

Offline Support: Cache data for offline usage

Social Features: Commenting and discussion on issues

AI Integration: Automatic issue categorization using ML

Multi-language Support: Support for multiple languages

Admin Dashboard: Web interface for municipal authorities

Analytics: Usage statistics and issue trends


Contributing

We welcome contributions! Please feel free to submit pull requests, report bugs, or suggest new features.

Fork the repository

Create a feature branch (git checkout -b feature/amazing-feature)

Commit your changes (git commit -m 'Add amazing feature')

Push to the branch (git push origin feature/amazing-feature)

Open a Pull Request

Credits

Credits
Supabase: For the amazing open-source Firebase alternative

JetBrains: For Kotlin and excellent development tools

Google: For Android Jetpack Compose and Maps SDK

Material Design: For the beautiful design system

Acknowledgments
Thanks to all contributors who helped test and improve IssueSpotter

Special thanks to the open-source community for various libraries used

Contact
Email: baranwalaryan23@gmail.com

LinkedIn:(https://www.linkedin.com/in/aryan-baranwal-75b9b129a?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app)

Civic Track - Making communities better, one issue at a time! 
