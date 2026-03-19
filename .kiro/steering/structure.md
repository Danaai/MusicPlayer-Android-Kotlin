# Project Structure

## Root Level
- `build.gradle.kts` - Root build configuration
- `settings.gradle.kts` - Project settings and module inclusion
- `gradle/` - Gradle wrapper and version catalog
- `local.properties` - Local SDK paths (not in version control)

## App Module (`app/`)
Standard Android app module structure with Kotlin and Compose setup.

### Source Structure (`app/src/`)
```
app/src/
├── main/
│   ├── java/com/example/musicapp/
│   │   ├── MainActivity.kt              # Main entry point
│   │   └── ui/theme/                    # Theme and styling
│   │       ├── Color.kt                 # Color definitions
│   │       ├── Theme.kt                 # Material theme setup
│   │       └── Type.kt                  # Typography definitions
│   ├── res/                             # Android resources
│   │   ├── drawable/                    # Vector drawables, icons
│   │   ├── mipmap-*/                    # App launcher icons
│   │   ├── values/                      # Strings, colors, themes
│   │   └── xml/                         # Backup and data extraction rules
│   └── AndroidManifest.xml              # App manifest
├── test/                                # Unit tests
└── androidTest/                         # Instrumented tests
```

## Package Organization
- **Base package**: `com.example.musicapp`
- **UI package**: `com.example.musicapp.ui.theme` (theming components)

## Naming Conventions
- **Files**: PascalCase for classes (`MainActivity.kt`)
- **Packages**: lowercase with dots (`com.example.musicapp`)
- **Resources**: snake_case (`ic_launcher_background.xml`)
- **Gradle files**: kebab-case for properties in TOML

## Architecture Guidelines
- **Single Activity**: Uses `MainActivity` with Compose navigation
- **Theme Structure**: Centralized in `ui.theme` package
- **Material Design 3**: Uses Material 3 components and theming
- **Edge-to-edge**: Implemented with `enableEdgeToEdge()` and proper padding

## Resource Organization
- **Drawables**: Vector icons and launcher resources
- **Mipmaps**: App launcher icons in multiple densities
- **Values**: Centralized strings, colors, and theme definitions
- **XML**: Configuration files for backup and data extraction

## Testing Structure
- **Unit tests**: `app/src/test/` - JUnit tests
- **Integration tests**: `app/src/androidTest/` - Espresso and Compose UI tests
- **Test naming**: Mirror main source package structure