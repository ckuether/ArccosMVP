# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform project targeting Android and iOS using Compose Multiplatform. The project follows a standard KMP structure with shared business logic and platform-specific implementations.

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
```

### Development Workflow
- Main branch: `main`
- Feature branches should be created from `main`
- Current active branch: `target-shot` (feature development)

## Project Architecture

### Module Structure

**composeApp**: Main application module containing:
- `commonMain`: Shared UI code using Compose Multiplatform
- `androidMain`: Android-specific platform code and entry points
- `iosMain`: iOS-specific platform code and bindings
- Namespace: `org.example.arccosmvp`

**shared**: Shared business logic module containing:
- `commonMain`: Platform-agnostic business logic, data models, and utilities
- `androidMain`: Android-specific implementations
- `iosMain`: iOS-specific implementations (exports as `Shared` framework)
- Namespace: `com.example.shared`

**location**: Location services module containing:
- Cross-platform location tracking and permission handling
- Platform-specific implementations for Android and iOS location services
- Background location service implementations

**core-ui**: Core UI components module containing:
- Shared UI components and theme definitions
- Platform-specific implementations for MapView and other UI components
- Design system resources (colors, dimensions, fonts)

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