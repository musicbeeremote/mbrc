<!-- Shields -->
![CI](https://github.com/musicbeeremote/mbrc/workflows/CI/badge.svg)
[![codecov](https://codecov.io/gh/musicbeeremote/mbrc/branch/main/graph/badge.svg)](https://codecov.io/gh/musicbeeremote/mbrc)
![GitHub](https://img.shields.io/github/license/musicbeeremote/mbrc.svg)
![GitHub release](https://img.shields.io/github/release/musicbeeremote/mbrc.svg)
[![Discord](https://img.shields.io/discord/420977901215678474.svg?style=popout)](https://discordapp.com/invite/rceTb57)

<br/>
<p align="center">
    <a href="https://github.com/musicbeeremote/mbrc">
    <img src="logo.png" alt="Logo" width="80"   height="80" />
    </a>

<h3 align="center">MusicBee Remote</h3>
    <p align="center">
        Application for controlling MusicBee through your Android Device 
        <br/>
        <a href="https://play.google.com/store/apps/details?id=com.kelsos.mbrc">Play Store</a>
        <br/>
        <i>(Unavailable since January 2023, planned return in 2026)</i>
        <br/>
        <br/>
        <a href="https://mbrc.kelsos.net/help/">Help</a>
        ·
        <a href="http://getmusicbee.com/forum/index.php?topic=7221.new;topicseen#new">MusicBee Forum</a>
        ·
        <a href="https://github.com/musicbeeremote/mbrc/issues">Report Bug</a>
        ·
        <a href="https://github.com/musicbeeremote/mbrc/issues">Request Feature</a>
    </p>
</p>

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Development](#development)
  * [Build Variants](#build-variants)
  * [Common Commands](#common-commands)
  * [Screenshot Testing](#screenshot-testing)
* [Usage](#usage)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)

## About the Project

[![MusicBee Remote Screenshot][project-screenshot]](https://mbrc.kelsos.net)

MusicBee Remote is an application that allows you to control [MusicBee](http://getmusicbee.com/) player.
This is achieved by using a [plugin](https://github.com/musicbeeremote/mbrc-plugin) that acts as a server.
The plugin exposes a TCP socket server and uses a JSON based protocol to communicate with the Android application.

The application development started in 2011 for personal usage and then it was open sourced and released to Play Store so others could use it. The application was also presented as part of my thesis title "*Android and application development for mobile devices*".

### Built With

* [Kotlin](https://kotlinlang.org/) - Primary language
* [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern declarative UI toolkit with Material 3
* [Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html) - Asynchronous programming
* [Koin](https://insert-koin.io/) - Dependency injection
* [Room](https://developer.android.com/training/data-storage/room) - Local database with Paging 3 support
* [Moshi](https://github.com/square/moshi) - JSON serialization
* [Coil](https://coil-kt.github.io/coil/) - Image loading
* [Glance](https://developer.android.com/jetpack/compose/glance) - Compose-based app widgets
* [Media3](https://developer.android.com/media/media3) - Media session handling
* [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Preferences storage

#### Testing

* [MockK](https://mockk.io/) - Mocking framework
* [Robolectric](http://robolectric.org/) - Android unit testing
* [Turbine](https://github.com/cashapp/turbine) - Flow testing
* [Truth](https://truth.dev/) - Assertions

## Getting Started

In order to get started with the project as a developer there are a few steps you need to follow.

### Prerequisites

**Android Requirements:**
- Minimum: Android 6.0 (API 23 - Marshmallow)
- Target: Android 16 (API 36)

**Development Environment:**
- Android Studio (stable version preferred) should be installed and up to date

To get started with the the project you first have to clone the project.

```bash
git clone https://github.com/musicbeeremote/mbrc.git
```

Then you can open the project with Android Studio.

### Installation

There are several ways to install the application on your device:

1. **GitHub Releases** (Recommended) - Download the latest version from [releases](https://github.com/musicbeeremote/mbrc/releases). This version doesn't include crash reporting or analytics.

2. **Android Studio** - If you are a developer, you can build and install directly from Android Studio.

3. **Play Store** - Currently unavailable (see note above), but planned to return in 2026. The Play Store version includes Firebase and Crashlytics for crash reporting and anonymous analytics.

## Development

### Build Variants

The project has two product flavors:

- **github** - Clean build without Firebase/Crashlytics, suitable for privacy-conscious users
- **play** - Play Store build with Firebase/Crashlytics for crash reporting and analytics

### Common Commands

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run static analysis (detekt + lint)
./gradlew staticAnalysis

# Run all local checks (format, lint, detekt, unit tests, screenshot tests)
./gradlew verifyLocal

# Run all checks including instrumentation tests
./gradlew verifyAll

# Generate test coverage report
./gradlew koverHtmlReport

# Check for dependency updates
./gradlew dependencyUpdates

# Format code
./gradlew formatKotlin

# Check code formatting
./gradlew lintKotlin
```

### Screenshot Testing

The project uses Google Compose Preview Screenshot Testing for visual regression testing.

```bash
# Record reference screenshots (run after UI changes)
./gradlew updateGithubDebugScreenshotTest

# Validate screenshots against reference images
./gradlew validateGithubDebugScreenshotTest
```

Screenshot tests are located in `app/src/screenshotTest/kotlin/` and use `@PreviewTest` annotations on Composable preview functions. Reference images are stored in `app/src/screenshotTestGithubDebug/reference/`.

## Usage

In order to use the application you need a working WiFi connection, that doesn't have [access point isolation](https://www.howtogeek.com/179089/lock-down-your-wi-fi-network-with-your-routers-wireless-isolation-option/).

You have to first install the [plugin](https://github.com/musicbeeremote/mbrc-plugin).

After installing the plugin if you are not prompted to allow MusicBee or the plugin through the Windows Firewall you might have to manually configure Windows Firewall to allow the plugin to receive connections.

For more detailed information you can check the [help](https://mbrc.kelsos.net/help/) page.

## Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on:

- How to report bugs and suggest features
- Development setup and code style
- Testing requirements
- Pull request process

See the [open issues](https://github.com/musicbeeremote/mbrc/issues) for a list of known issues and feature requests.

## License

The source code of the application is licensed under the [GPLv3](https://www.gnu.org/licenses/gpl.html) license. See `LICENSE` for more information.

    MusicBee Remote (for Android)
    Copyright (C) 2011-2025  Konstantinos Paparas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Contact

MusicBee Remote - [@MusicBeeRemote](https://twitter.com/musicbeeremote)

Project Link: [https://github.com/musicbeeremote/mbrc](https://github.com/musicbeeremote/mbrc)

## Acknowledgements

* [Tasos Papazoglou Chalikias](https://github.com/sushiperv)

Tasos created many of the icons, and was responsible for the Holo design of the application. Most of the icons are licenced under the [Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.](https://creativecommons.org/licenses/by-nc-nd/3.0/deed.en_US).

* [Jordan Georgiades](https://www.linkedin.com/in/jordan-georgiadis)

Jordan is responsible for the conception and creation of the application logo.

* Carlos Parga

Carlos created some really nice [mockups](https://groups.google.com/forum/#!topic/musicbee-remote/wgm029yfJnU) from where I drew inspiration while working on the UI.

* [Cyanogen Apollo player](https://github.com/CyanogenMod/android_packages_apps_Apollo)

Apollo Player was one of the original sources of inspiration for the UI of MusicBee Remote

* [Google Play Music for Android](https://play.google.com/store/apps/details?id=com.google.android.music)

[project-screenshot]: https://raw.githubusercontent.com/musicbeeremote/mbrc/main/screenshot.jpg
