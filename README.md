# 📈 Habit Tracker App

Track your habits, measure your progress, and become the disciplined machine you were born to be. Whether you're trying to drink 2L of water a day or do 100 pushups, this app has your back.

---

## ✨ Features

* ✅ **Create Habits**: Add new habits to track—daily, weekly, or unit-based (like “Drink 2L water” or “Read 30 pages”).
* 📅 **Progress Logging**: Mark habits as done or log how much you’ve completed in measurable units.
* 📊 **Visual Feedback**: See your progress through beautiful bar graphs.
* 🧠 **Performance Metrics**: Get insights on how well you're sticking to your goals.
* 🔄 **Sync Support**: Sync your data to the cloud with **Firestore**.
* 🏠 **Offline-First**: Data is stored locally using **Room Database**—you’re covered even when you're off-grid.
* 🧱 Built with **Jetpack Compose** for modern UI and **ViewModels** for clean state management.

---

## 📸 Screenshots

> *Because words can't flex like visuals do.*

| Home Screen                          | Habit Detail                             | Graph & Metrics                        |
| ------------------------------------ | ---------------------------------------- | -------------------------------------- |
| ![Home Screen](screenshots/home.png) | ![Detail Screen](screenshots/detail.png) | ![Graph Screen](screenshots/graph.png) |

---

## 🛠️ Tech Stack

* **Jetpack Compose** – Modern declarative UI toolkit.
* **ViewModel** – Lifecycle-aware state management.
* **Room Database** – Local data persistence.
* **Firestore** – Cloud sync support.
* **Kotlin** – Pure idiomatic Kotlin, of course!

---

## 🚀 How to Build

1. Clone the repo:

   ```bash
   git clone https://github.com/yourusername/habit-tracker-app.git
   ```
2. Open in **Android Studio**.
3. Run the app on your emulator or device.
4. Start conquering your goals, one habit at a time.

---

## 📁 Folder Structure

# 📁 Project Folder Structure (MVVM + Firebase + Room + Clean Architecture)

```plaintext

com.project.socialhabittracker
│
├── data
│   ├── local                     # Room database layer
│   │   ├── dao                   # Data Access Objects
│   │   ├── entities              # Entity classes
│   │   └── AppDatabase.kt        # Room DB setup
│   │
│   ├── remote                    # Firebase-related logic
│   │   ├── auth
│   │   │   ├── FirebaseAuthRepository.kt         # Auth interface
│   │   │   └── FirebaseAuthRepositoryImpl.kt     # Auth implementation
│   │   │
│   │   ├── firestore
│   │   │   ├── FirestoreUserRepository.kt        # Firestore interface
│   │   │   └── FirestoreUserRepositoryImpl.kt    # Firestore implementation
│   │
│   └── repository
│       └── UserRepository.kt     # Combines local + remote (optional)
│
├── domain                        # Business logic layer (optional)
│   ├── model                     # Clean models (UI-friendly)
│   └── usecase                   # Use cases (e.g., SaveUserUseCase.kt)
│
├── ui
│   ├── login
│   │   ├── LoginViewModel.kt
│   │   └── LoginScreen.kt
│   │
│   ├── home
│   │   ├── HomeViewModel.kt
│   │   └── HomeScreen.kt
│   │
│   └── components                # Reusable Jetpack Compose components
│
├── utils
│   ├── Extensions.kt             # Extension functions
│   └── Constants.kt              # App-wide constants
│
└── di
└── AppModule.kt              # Hilt dependency injection module

```

---

## 💡 Future Enhancements

* Notification reminders
* Streak rewards system
* Dark mode (because night owls matter too)
* More detailed analytics

---

## 🧠 Philosophy

*"We are what we repeatedly do. Excellence, then, is not an act, but a habit."*
— Aristotle (or possibly your future self, after using this app)
