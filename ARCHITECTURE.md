# Architecture

This document describes the architecture of the MusicBee Remote Android application.

## Overview

MusicBee Remote is an Android app for controlling MusicBee music player via a companion plugin. The app communicates with the plugin over TCP using a JSON-based protocol.

## Module Structure

```
app/                    # Main application, navigation, DI setup
│
├── core/
│   ├── common/        # Shared utilities, state classes, base classes
│   ├── ui/            # Design system, theme, shared Compose components
│   ├── data/          # Room database, repositories, entities
│   ├── networking/    # TCP client, protocol handling, message serialization
│   ├── platform/      # Android platform integrations (notifications, services)
│   └── queue/         # Queue management use cases
│
└── feature/
    ├── library/       # Library browsing (artists, albums, tracks, genres)
    ├── playback/      # Player controls, now playing, lyrics
    ├── settings/      # App settings, connection management
    ├── content/       # Radio stations, playlists
    ├── misc/          # Output selection, miscellaneous features
    ├── minicontrol/   # Mini player control bar
    └── widgets/       # Home screen widgets (Glance)
```

### Dependency Rules

- `app` depends on all `feature/*` and `core/*` modules
- `feature/*` modules depend on `core/*` modules only
- `core/*` modules depend on `core/common` only
- No circular dependencies between modules

## Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │
│  Jetpack Compose screens and components                     │
│  - Stateless composables receive state, emit events         │
│  - collectAsStateWithLifecycle() for Flow collection        │
├─────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│  - Exposes UI state via StateFlow                           │
│  - Handles user actions                                     │
│  - Coordinates between repositories and use cases           │
├─────────────────────────────────────────────────────────────┤
│                    Repository Layer                         │
│  - Single source of truth for data                          │
│  - Coordinates between remote and local data sources        │
│  - Exposes data as Flow for reactive updates                │
├─────────────────────────────────────────────────────────────┤
│                    Data Source Layer                        │
│  - Room database (local persistence)                        │
│  - TCP client (remote MusicBee plugin)                      │
└─────────────────────────────────────────────────────────────┘
```

## State Management

### Global State

Global application state is managed through dedicated StateFlow holders:

- **`AppState` / `AppStateFlow`**: Current playing track, position, player status
- **`ConnectionStateFlow`**: Connection status to MusicBee plugin

These are injected where needed and provide reactive updates across the app.

### Feature State

Each feature uses ViewModels with:

- `StateFlow<T>` for UI state
- `SharedFlow<UiMessage>` for one-time events (snackbars, navigation)

Example pattern:
```kotlin
class PlayerViewModel(...) : ViewModel() {
    private val _state = MutableStateFlow(PlayerState())
    val state: StateFlow<PlayerState> = _state.asStateFlow()
}
```

## Protocol Communication

The app communicates with MusicBee via a TCP socket connection:

```
┌──────────────┐         TCP/JSON          ┌──────────────────┐
│  Android App │ ◄─────────────────────────► │  MusicBee Plugin │
└──────────────┘                            └──────────────────┘
```

### Message Flow

1. **Outgoing**: `UserActionUseCase` → `ClientConnectionUseCase` → TCP Socket
2. **Incoming**: TCP Socket → Message Handler → State Updates → UI

### Protocol Format

Messages are JSON-serialized using Moshi:
```json
{"context": "player", "data": "Android"}
```

## Dependency Injection

Koin is used for dependency injection:

- Module definitions in each feature/core module
- `viewModelOf()` for ViewModel registration
- `koinViewModel()` in Compose for ViewModel retrieval

### Coroutine Dispatchers

Always inject `AppCoroutineDispatchers` instead of using `Dispatchers.*` directly:

```kotlin
class MyRepository(private val dispatchers: AppCoroutineDispatchers) {
    suspend fun fetch() = withContext(dispatchers.io) { ... }
}
```

This enables testing with `TestCoroutineDispatcher`.

## Data Persistence

### Room Database

- Entities for library content (tracks, albums, artists, genres)
- DAOs return `Flow<T>` for reactive updates
- Paging 3 integration for large lists

### Paging

Large lists use Paging 3:
```kotlin
fun getAll(): Flow<PagingData<Track>> =
    Pager(PagingConfig(pageSize = 50)) { dao.getAll() }.flow
```

## Navigation

Single-Activity architecture with Compose Navigation:

- `NavHost` in `RemoteApp.kt`
- Feature screens are navigation destinations
- Bottom navigation for main sections

## Key Patterns

| Pattern | Usage |
|---------|-------|
| MVVM | UI ↔ ViewModel ↔ Repository |
| Repository | Data access abstraction |
| Use Case | Single business operations (e.g., `PathQueueUseCase`) |
| StateFlow | Reactive UI state |
| Paging 3 | Large list handling |

## Testing

- **Unit tests**: MockK for mocking, Turbine for Flow testing
- **Screenshot tests**: Compose screenshot testing for UI verification
- **Robolectric**: Android framework testing without device
