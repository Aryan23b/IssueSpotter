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

Screenshots
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
