# Technology Stack

## Build System
- **Gradle** with Kotlin DSL (`.gradle.kts`)
- **Android Gradle Plugin** 8.13.2
- **Kotlin** 2.0.21 with Compose compiler plugin
- **Version Catalog** (`libs.versions.toml`) for dependency management

## Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Target SDK**: 36, Min SDK: 24
- **Java Version**: 11

## Key Dependencies
- **Compose BOM**: 2024.09.00 (manages all Compose library versions)
- **Navigation**: Navigation Compose 2.7.4
- **Architecture**: 
  - ViewModel Compose integration
  - Lifecycle Runtime Compose
- **Dependency Injection**: Hilt 2.48 with Navigation Compose integration
- **Database**: Room 2.6.0 with Kotlin extensions
- **Networking**: Retrofit 2.9.0 with Gson converter and OkHttp logging
- **Media**: ExoPlayer (Media3) 1.2.0 with UI and session support
- **Image Loading**: Coil Compose 2.5.0
- **Data Storage**: DataStore Preferences 1.0.0
- **Async**: Coroutines Android 1.7.3

## Common Commands

### Build & Run
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug

# Run app on connected device/emulator
./gradlew installDebug && adb shell am start -n com.example.musicapp/.MainActivity
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.example.musicapp.ExampleUnitTest"
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

## Architecture Notes
- Uses KAPT for annotation processing (Room, Hilt)
- ProGuard disabled in debug, can be enabled for release builds
- Supports dynamic theming on Android 12+
- Edge-to-edge display implementation