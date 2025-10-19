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

### Dependency Injection Architecture

**Koin Configuration:**
- Modular DI setup with separate modules for each feature (e.g., `roundOfGolfDomainModule`, `locationDomainModule`)
- Platform-specific initialization: Android (`GolfApp.kt`) vs iOS (`MainViewController.kt`)
- All feature domain modules must be included in Koin initialization or dependency resolution will fail
- Use Case injection pattern: Domain modules provide Use Cases, Presentation modules consume them

### Key Dependencies
- Kotlin 2.2.20 with Compose Multiplatform 1.9.0
- Room Database 2.7.0 with KSP 2.2.10-2.0.2 for cross-platform data persistence
- Koin 4.1.1 for dependency injection across all modules
- Material3 design system with custom theming
- Google Maps Compose 4.4.1 with Play Services Maps 18.2.0
- Coil 3.3.0 for image loading with SVG support
- Jetpack Navigation Compose for routing

### Platform Targets
- Android: Min SDK 26, Target/Compile SDK 36, Java 17
- iOS: Static frameworks generated for each feature module (iosArm64, iosSimulatorArm64)

## Development Notes

### Data Layer Architecture
- **Room Database** with entities: `ScoreCardEntity`, `RoundOfGolfEventEntity`
- Database version 2 with proper migration handling
- Schemas exported to `shared/schemas/` directory
- Repository pattern implemented in shared module

### UI Architecture
- **Clean separation**: Components in `/presentation/components/` following Single Responsibility Principle
- **State management**: ViewModels in presentation modules, UI state with Lifecycle-aware Compose APIs
- **Navigation**: Route constants in shared module, navigation logic in composeApp
- **Theming**: Material3 with LocalDimensionResources for consistent spacing

### Key Architectural Patterns
- **Use Case Pattern**: Domain logic encapsulated in Use Cases (e.g., `TrackSingleRoundEventUseCase`, `TrackMultipleRoundEventsUseCase`, `GetRoundEventsUseCase`)
- **Repository Pattern**: Data access abstracted through repositories in shared module
- **MVVM Pattern**: ViewModels coordinate between Use Cases and UI components
- **Expect/Actual Pattern**: Platform-specific implementations for location services and map integration

## Testing Commands

```shell
# Run specific module tests
./gradlew :shared:testDebugUnitTest
./gradlew :round-of-golf:round-of-golf-domain:testDebugUnitTest
./gradlew :location:location-domain:testDebugUnitTest

# Run Android device tests
./gradlew :shared:connectedAndroidTest
```