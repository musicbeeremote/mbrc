# Contributing to MusicBee Remote

Thank you for your interest in contributing to MusicBee Remote! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Features](#suggesting-features)
  - [Code Contributions](#code-contributions)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Testing](#testing)
- [Pull Request Process](#pull-request-process)
- [Commit Messages](#commit-messages)

## Code of Conduct

Please be respectful and considerate in all interactions. We want this to be a welcoming community for everyone.

## Getting Started

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/mbrc.git
   ```
3. Add the upstream remote:
   ```bash
   git remote add upstream https://github.com/musicbeeremote/mbrc.git
   ```
4. Create a branch for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## How to Contribute

### Reporting Bugs

Before submitting a bug report:

1. Check the [existing issues](https://github.com/musicbeeremote/mbrc/issues) to avoid duplicates
2. Ensure you're using the latest version

When reporting a bug, include:

- Android version and device model
- App version
- MusicBee and plugin version
- Steps to reproduce the issue
- Expected vs actual behavior
- Screenshots or logs if applicable

### Suggesting Features

Feature requests are welcome! Please:

1. Check existing issues and discussions first
2. Clearly describe the feature and its use case
3. Explain why this would benefit users

### Code Contributions

We welcome code contributions for:

- Bug fixes
- New features
- Performance improvements
- Documentation improvements
- Test coverage improvements

## Development Setup

### Prerequisites

- Android Studio (latest stable version)
- JDK 17 or higher
- Android SDK with API 36

### Building the Project

```bash
# Build the project
./gradlew build

# Install debug version on connected device
./gradlew installGithubDebug
```

### Build Variants

- **github** - Clean build without Firebase/Crashlytics (recommended for development)
- **play** - Play Store build with Firebase/Crashlytics

## Code Style

### General Guidelines

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Keep functions small and focused
- Prefer composition over inheritance
- Use extension functions for utility methods

### Formatting

The project uses ktlint for code formatting:

```bash
# Format code
./gradlew formatKotlin

# Check formatting
./gradlew lintKotlin
```

### Architecture

- **MVVM** with Repository pattern
- **Dependency Injection** using Koin
- **UI** built with Jetpack Compose and Material 3
- **State Management** using StateFlow (no LiveData)
- **Async** using Kotlin Coroutines

### Important Patterns

#### Coroutine Dispatchers

Always inject `AppCoroutineDispatchers` instead of using `Dispatchers` directly:

```kotlin
// Correct
class MyViewModel(
    private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
    fun load() = viewModelScope.launch {
        withContext(dispatchers.io) { /* ... */ }
    }
}

// Incorrect - don't do this
class MyViewModel : ViewModel() {
    fun load() = viewModelScope.launch {
        withContext(Dispatchers.IO) { /* ... */ }  // Not testable!
    }
}
```

#### ViewModels

- Use `StateFlow` for exposing state to Compose UI
- Use `UiMessageQueue` for one-time UI events
- Handle loading and error states properly

#### Compose UI

- Don't put business logic in Composables
- Use `collectAsStateWithLifecycle()` for collecting flows
- Follow existing screen patterns in `features/`

## Testing

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run specific test class
./gradlew test --tests "*ViewModelTest"

# Run all local checks
./gradlew verifyLocal

# Generate coverage report
./gradlew koverHtmlReport
```

### Screenshot Tests

```bash
# Record reference screenshots
./gradlew updateGithubDebugScreenshotTest

# Validate screenshots
./gradlew validateGithubDebugScreenshotTest
```

### Testing Guidelines

- Write unit tests for ViewModels and Repositories
- Use MockK for mocking dependencies
- Use Turbine for testing Flow emissions
- Use `runTest` for coroutine testing
- Test edge cases and error scenarios

## Pull Request Process

1. **Ensure your code builds** and all tests pass:
   ```bash
   ./gradlew verifyLocal
   ```

2. **Update documentation** if needed

3. **Create a pull request** with:
   - Clear title describing the change
   - Description of what and why
   - Reference to related issues (e.g., "Closes #123")

4. **Address review feedback** promptly

5. **Squash commits** if requested, or keep them clean and logical

### PR Checklist

- [ ] Code follows the project's style guidelines
- [ ] Tests added/updated for new functionality
- [ ] All tests pass locally
- [ ] No new lint warnings introduced
- [ ] Documentation updated if needed

## Commit Messages

Follow conventional commit format:

```
type: short description

Optional longer description explaining what and why.

Closes #123
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring (no functional change)
- `test`: Adding or updating tests
- `docs`: Documentation changes
- `chore`: Build, CI, or tooling changes
- `style`: Formatting changes (no code change)

### Examples

```
feat: add dark mode toggle to settings

Implements user preference for theme selection with
light, dark, and system default options.

Closes #61
```

```
fix: correct elapsed time display for streaming content

The elapsed time was not updating for streaming tracks
because the duration check was incorrect.

Closes #115
```

## Questions?

If you have questions, feel free to:

- Open a [discussion](https://github.com/musicbeeremote/mbrc/discussions)
- Join our [Discord](https://discordapp.com/invite/rceTb57)
- Ask on the [MusicBee Forum](http://getmusicbee.com/forum/index.php?topic=7221.new)

Thank you for contributing!