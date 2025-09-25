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
- `iosMain`: iOS-specific implementations (exports as `sharedKit` framework)
- Namespace: `com.example.shared`

### Key Dependencies
- Kotlin 2.2.20 with Compose Multiplatform 1.9.0
- AndroidX Lifecycle and Activity Compose
- Kotlinx Serialization, Coroutines, and DateTime
- Material3 design system

### Platform Targets
- Android: Min SDK 24, Target SDK 36, Compile SDK 36
- iOS: arm64 and simulator arm64 architectures
- JVM target: Java 11

## Development Notes

- The app uses Compose Multiplatform for shared UI across platforms
- Business logic is separated into the `shared` module for maximum code reuse
- Platform-specific implementations use the expect/actual pattern
- iOS framework exports as `sharedKit` for integration with Xcode projects
- Configuration cache and build cache are enabled for faster builds