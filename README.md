# 🎬 Compose Video Player

A fully-featured **local video player** for Android, built entirely with **Jetpack Compose** and powered by **ExoPlayer**. Browse all videos stored on your device, play them with custom controls, and manage your library — all from a clean, modern UI.

---
## 📥 Download

> Get the latest APK and install it directly on your Android device.

<p align="center">
  <a href="https://github.com/lipun-dev/Video_player/releases/download/VideoPlayer/app-debug.apk">
    <img src="https://img.shields.io/badge/Download-APK-brightgreen?style=for-the-badge&logo=android" alt="Download APK"/>
  </a>
</p>

## 📸 Screenshots


<p align="center">
  <img src="HomeScreen.jpeg" width="220" alt="Home Screen"/>
  &nbsp;&nbsp;
  <img src="videoPlayerScreen.jpeg" width="220" alt="Player Screen"/>
  &nbsp;&nbsp;
  <img src="AllVideoScreen.jpeg" width="220" alt="AllVideoScreen"/>
</p>

<p align="center">
  <img src="FolderVideoScreen.jpeg" width="220" alt="FolderVideoScreen"/>
  &nbsp;&nbsp;
  <img src="ScreenWithBottomBar.jpeg" width="220" alt="ScreenWithBottomBar"/>
  &nbsp;&nbsp;
</p>

---

## 🎥 Demo

<div align="center">

  https://github.com/user-attachments/assets/b2577a99-ae47-44d3-b17c-bab7e5a90c63
</div>


---

## ✨ Features

- **📂 Local Video Discovery** — Uses `MediaStore` cursor queries to automatically scan and list all video files available on the device storage.
- **▶️ Smooth Video Playback** — Integrates **ExoPlayer** inside Jetpack Compose via `AndroidView` for reliable, high-performance video rendering.
- **🎮 Custom Video Controls** — Fully custom-built playback controls using Compose UI, including:
  - Play / Pause toggle
  - Seek bar with current position and total duration
  - Fast-forward and rewind
  - Fullscreen toggle
- **🔗 Share Videos** — Quickly share any video from your library to other apps using Android's share intent.
- **🗑️ Delete Videos** — Remove unwanted videos from local storage directly within the app, with a confirmation prompt to prevent accidental deletion.
- **✏️ Rename Videos** — Rename video files on the fly using a clean dialog interface, with the library refreshing automatically afterward.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Video Player | ExoPlayer (Media3) |
| Video Discovery | MediaStore + ContentResolver (Cursor) |
| Architecture | MVVM |
| State Management | `StateFlow` / `collectAsState` |
| Async | Kotlin Coroutines |

---

## 🏗️ Project Structure
```
app/
├── data/
│   └── MediaStoreHelper.kt       # Cursor-based video fetching from MediaStore
├── model/
│   └── VideoItem.kt              # Data model representing a video file
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt         # Video library listing screen
│   │   └── PlayerScreen.kt       # Playback screen with ExoPlayer + AndroidView
│   ├── components/
│   │   ├── VideoControls.kt      # Custom playback control composables
│   │   ├── VideoCard.kt          # Individual video item card with options menu
│   │   └── RenameDialog.kt       # Rename input dialog composable
│   └── theme/
│       └── Theme.kt              # App theming
├── viewmodel/
│   ├── HomeViewModel.kt          # Manages video list, delete, rename logic
│   └── PlayerViewModel.kt        # Manages ExoPlayer lifecycle and state
└── MainActivity.kt
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or later
- Android SDK **26+**
- Kotlin **1.9+**

### Installation

1. **Clone the repository**
```bash
   git clone https://github.com/your-username/compose-video-player.git
   cd compose-video-player
```

2. **Open in Android Studio**
   - File → Open → select the cloned folder

3. **Build & Run**
   - Connect a physical device or start an emulator
   - Click **Run ▶** or press `Shift + F10`

---

## 🔐 Permissions

The app requires the following permissions declared in `AndroidManifest.xml`:
```xml
<!-- For Android 12 and below -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- For Android 13+ -->
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<!-- For deleting/renaming files on older APIs -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="29" />

```

Runtime permission handling is implemented to ensure compatibility across Android 10–14+.

---

## ⚙️ How It Works

### Video Discovery via MediaStore
```kotlin
val cursor = context.contentResolver.query(
    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
    projection,
    null, null,
    "${MediaStore.Video.Media.DATE_ADDED} DESC"
)
cursor?.use {
    while (it.moveToNext()) {
        // Read video metadata from cursor columns
    }
}
```

### ExoPlayer inside Compose via AndroidView
```kotlin
AndroidView(
    factory = { ctx ->
        PlayerView(ctx).apply {
            player = exoPlayer
            useController = false  // Custom controls used instead
        }
    },
    modifier = Modifier.fillMaxSize()
)
```

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create your feature branch — `git checkout -b feature/amazing-feature`
3. Commit your changes — `git commit -m 'Add amazing feature'`
4. Push to the branch — `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 📄 License
```
MIT License

Copyright (c) 2025 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👨‍💻 Author

**S LIPUN DAS**
- GitHub: [@lipun-dev](https://github.com/lipun-dev)
- Gmail: [slipundas8888@gmail.com](slipundas8888@gmail.com)

---

<p align="center">⭐ Star this repo if you found it useful!</p>
