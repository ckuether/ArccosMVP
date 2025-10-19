# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project targeting Android and iOS using Compose Multiplatform. The project follows a **feature-based modular clean architecture** with domain and presentation layers separated into distinct modules.

## Build Commands

### Android
```shell
# Build debug APK
./gradlew :composeApp:assembleDebug

# Build release APK  
./gradlew :composeApp:assembleRelease

# Install debug APK to connected device
./gradlew :composeApp:installDebug

# Run tests
./gradlew :composeApp:testDebugUnitTest
./gradlew :shared:testDebugUnitTest
```

### iOS
- Use Xcode to open the `iosApp` directory
- Build and run from Xcode, or use IDE run configurations
- iOS framework is built automatically when building the iOS app

### General
```shell
# Clean build
./gradlew clean

# Build all targets
./gradlew build

# Run all tests
./gradlew test

# Build specific feature modules
./gradlew :round-of-golf:round-of-golf-domain:build
./gradlew :location:location-presentation:build
```

### Development Workflow
- Main branch: `main`
- Feature branches should be created from `main`
- Current active branch: `round-of-golf-module` (feature development)

## Project Architecture

### Modular Clean Architecture

The project uses a **feature-based modular architecture** where each feature has separate domain and presentation modules:

**Core Modules:**
- `composeApp` - Main application with navigation, app-level DI, and platform initialization
- `shared` - Core business logic, database entities, repositories, and foundational services
- `core-ui` - Design system, shared UI components, theme definitions, and resources

**Feature Modules (Domain + Presentation):**
- `location:location-domain` - Location services business logic, use cases, and domain models
- `location:location-presentation` - Location UI components, ViewModels, and platform-specific implementations
- `round-of-golf:round-of-golf-domain` - Golf round tracking business logic and use cases  
- `round-of-golf:round-of-golf-presentation` - Golf round UI components and ViewModels

**Module Dependency Graph:**
```
composeApp
├── shared (database, repositories, core domain)
├── core-ui (design system)  
├── location:location-domain
├── location:location-presentation (depends on location-domain)
├── round-of-golf:round-of-golf-domain
└── round-of-golf:round-of-golf-presentation (depends on round-of-golf-domain)
```

### Build System Architecture

**BuildSrc Module:**
- `Modules.kt` provides type-safe module references (use `Modules.locationDomain` instead of hardcoded strings)
- Version catalog in `gradle/libs.versions.toml` centralizes dependency management
- Configuration cache and build cache enabled for faster builds

### Key Dependencies
- Kotlin 2.2.20 with Compose Multiplatform 1.9.0
- AndroidX Lifecycle and Activity Compose
- Kotlinx Serialization, Coroutines, and DateTime
- Material3 design system
- Room Database with KSP for cross-platform data persistence
- Koin for dependency injection
- Google Maps and location services
- Coil for image loading with SVG support

### Platform Targets
- Android: Min SDK 26, Target SDK 36, Compile SDK 36, Java 17
- iOS: iosArm64, iosSimulatorArm64, iosX64 architectures

## Development Notes

- The app uses Compose Multiplatform for shared UI across platforms
- Business logic is separated into the `shared` module for maximum code reuse
- Platform-specific implementations use the expect/actual pattern
- iOS framework exports as `Shared` for integration with Xcode projects
- Configuration cache and build cache are enabled for faster builds
- Room database schemas are stored in `shared/schemas/` directory
- Google Maps API key is configured in `gradle.properties` and injected via manifest placeholders

### Key Architectural Patterns
- **MapView Integration**: Platform-specific map implementations with screen projection utilities for coordinate calculations
- **Location Services**: Cross-platform location tracking with background service support
- **Golf Course Features**: Target markers, yardage buttons, and hole visualization using map projections
- **Data Persistence**: Room database with KSP for schema generation across platforms

## Testing Commands

```shell
# Run specific module tests
./gradlew :shared:testDebugUnitTest
./gradlew :location:testDebugUnitTest
./gradlew :core-ui:testDebugUnitTest

# Run Android device tests
./gradlew :shared:connectedAndroidTest
```