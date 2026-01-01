BluPass Application Documentation
Project Name: BluPass Platform: Android (Mobile) Version: 1.0.0

1. Project Overview
BluPass is a secure Android mobile application designed to streamline community management and access control. It serves as a digital bridge between Homeowners and Community Administrators, facilitating verified registration, residency status tracking, and secure management of resident data.
The system addresses the issue of manual resident tracking by digitizing the homeowner application and approval process.
•	For Homeowners: Provides a digital identity and real-time status tracking for their residency application.
•	For Admins: A dedicated dashboard to review, approve, or reject homeowner requests efficiently using real-time data synchronization.

2. Key Features
2.1 User Role Management
The application supports distinct workflows for different user types:
•	Administrators: Have privileged access to manage the database and approve/reject requests.
•	Homeowners: Can register, view their status, and eventually access community features upon approval.
2.2 Real-time Registration System
Users sign up with essential details (Email, Mobile, Lot Number). Upon registration, accounts are set to a default pending state, preventing unauthorized access until verified.
2.3 Admin Dashboard & Control
•	Pending Request View: Admins can view a live list of all homeowners awaiting approval.
•	Approval Logic: Validates user data and updates the database status to approved.
•	Rejection Logic: Allows admins to remove invalid or unauthorized entries from the database.
2.4 MVP Architecture & Dynamic UI
The app is built on the Model-View-Presenter (MVP) pattern, ensuring that the User Interface (UI) is decoupled from the business logic. The UI updates dynamically in real-time using RecyclerViews that listen to Firebase Database changes.

3. Technical Specifications
3.1 Tech Stack
•	Language: Kotlin
•	Frontend: Android XML Layouts (Material Design components)
•	Backend: Firebase Realtime Database
•	Authentication: Firebase Authentication
•	IDE: Android Studio
3.2 System Architecture (MVP)
The project strictly adheres to the Model-View-Presenter architecture to ensure scalability and testability.
1.	Model:
o	Handles data logic and interactions with Firebase.
o	Key Files: User.kt (Data Class), AdminRepository.kt (Database Operations).
2.	View:
o	Handles UI rendering and capturing user interactions.
o	Key Files: AdminManageActivity.kt, AdminManageView.kt (Interface).
3.	Presenter:
o	Acts as the intermediary. It retrieves data from the Model, applies business logic, and updates the View.
o	Key Files: AdminManagePresenter.kt.
4. Database Structure (NoSQL)
The application uses a Firebase Realtime Database JSON tree structure:
JSON
root
users
  ├── admins
  │    └── <Firebase-UID>  (e.g., sMVNHK...)
  │         ├── email: "adminaccc@gmail.com"
  │         ├── mobile: "9283235647"
  │         ├── role: "Admin"
  │         └── status: "approved"
  │
  └── homeowners
       └── <Firebase-UID>  (e.g., DglJAng...)
            ├── advertisedUuid: "0000a001-0000-1000-8000-00805f9b34fb"
            ├── email: "homeowner_3@gmail.com"
            ├── lotNumber: "S19"
            ├── mobile: "12312345645"
            ├── role: "Homeowner"
            ├── status: "approved"
            └── username: "home"
