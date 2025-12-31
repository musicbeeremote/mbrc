package com.kelsos.mbrc.features.library.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kelsos.mbrc.R
import com.kelsos.mbrc.app.ScreenConfig
import com.kelsos.mbrc.common.ui.compose.DrawerNavigationIcon
import com.kelsos.mbrc.common.ui.compose.RemoteTopAppBar
import com.kelsos.mbrc.features.library.LibraryStats
import com.kelsos.mbrc.features.library.LibrarySyncProgress
import com.kelsos.mbrc.features.library.LibraryUiEvent
import com.kelsos.mbrc.features.library.LibraryViewModel
import com.kelsos.mbrc.features.library.SyncResult
import com.kelsos.mbrc.features.library.albums.Album
import com.kelsos.mbrc.features.library.artists.Artist
import com.kelsos.mbrc.features.library.compose.tabs.AlbumsTab
import com.kelsos.mbrc.features.library.compose.tabs.ArtistsTab
import com.kelsos.mbrc.features.library.compose.tabs.GenresTab
import com.kelsos.mbrc.features.library.compose.tabs.TracksTab
import com.kelsos.mbrc.features.library.genres.Genre
import com.kelsos.mbrc.features.minicontrol.MiniControl
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
  onOpenDrawer: () -> Unit,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  onScreenConfigChange: (ScreenConfig) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LibraryViewModel = koinViewModel()
) {
  val scope = rememberCoroutineScope()

  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var menuExpanded by remember { mutableStateOf(false) }
  var statsToShow by remember { mutableStateOf<LibraryStats?>(null) }

  val albumArtistsOnly by viewModel.albumArtistsOnly.collectAsState(initial = false)
  val syncProgress by viewModel.progress.collectAsState(initial = LibrarySyncProgress.Idle)

  val pagerState = rememberPagerState(pageCount = { LibraryTab.entries.size })

  LibraryScreenConfigEffect(
    isSearchActive = isSearchActive,
    menuExpanded = menuExpanded,
    syncProgress = syncProgress,
    albumArtistsOnly = albumArtistsOnly,
    onOpenDrawer = onOpenDrawer,
    onSearchToggle = { isSearchActive = it },
    onSearchQueryChange = { query ->
      viewModel.search(query)
    },
    onSearchClear = {
      isSearchActive = false
      viewModel.search("")
    },
    onMenuToggle = { menuExpanded = it },
    onSync = { viewModel.sync() },
    onShowStats = { viewModel.displayLibraryStats() },
    onAlbumArtistsOnlyToggle = { viewModel.updateAlbumArtistOnly(!albumArtistsOnly) },
    onScreenConfigChange = onScreenConfigChange
  )

  LibraryEventsEffect(
    viewModel = viewModel,
    snackbarHostState = snackbarHostState,
    onStatsReady = { statsToShow = it }
  )

  statsToShow?.let { stats ->
    LibraryStatsDialog(stats = stats, onDismiss = { statsToShow = null })
  }

  LibraryContent(
    pagerState = pagerState,
    snackbarHostState = snackbarHostState,
    isSyncing = syncProgress.running,
    onNavigateToGenreArtists = onNavigateToGenreArtists,
    onNavigateToArtistAlbums = onNavigateToArtistAlbums,
    onNavigateToAlbumTracks = onNavigateToAlbumTracks,
    onNavigateToPlayer = onNavigateToPlayer,
    onSync = { viewModel.sync() },
    onTabClick = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
    modifier = modifier
  )
}

@Composable
private fun LibraryScreenConfigEffect(
  isSearchActive: Boolean,
  menuExpanded: Boolean,
  syncProgress: LibrarySyncProgress,
  albumArtistsOnly: Boolean,
  onOpenDrawer: () -> Unit,
  onSearchToggle: (Boolean) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSearchClear: () -> Unit,
  onMenuToggle: (Boolean) -> Unit,
  onSync: () -> Unit,
  onShowStats: () -> Unit,
  onAlbumArtistsOnlyToggle: () -> Unit,
  onScreenConfigChange: (ScreenConfig) -> Unit
) {
  val lifecycleOwner = LocalLifecycleOwner.current

  // Use rememberUpdatedState to capture latest values for the topBar lambda
  val currentMenuExpanded by rememberUpdatedState(menuExpanded)
  val currentSyncProgress by rememberUpdatedState(syncProgress)
  val currentIsSearchActive by rememberUpdatedState(isSearchActive)
  val currentAlbumArtistsOnly by rememberUpdatedState(albumArtistsOnly)

  // Track if screen is still mounted to prevent config updates during navigation
  var isMounted by remember { mutableStateOf(true) }
  DisposableEffect(Unit) {
    isMounted = true
    onDispose { isMounted = false }
  }

  // Push config changes when any of the state values change
  // Only push if lifecycle is at least STARTED to avoid updates during navigation
  LaunchedEffect(syncProgress, isSearchActive, menuExpanded, albumArtistsOnly) {
    val currentState = lifecycleOwner.lifecycle.currentState
    if (isMounted && currentState.isAtLeast(Lifecycle.State.STARTED)) {
      onScreenConfigChange(
        ScreenConfig(
          topBar = {
            LibraryTopBar(
              isSearchActive = currentIsSearchActive,
              menuExpanded = currentMenuExpanded,
              syncProgress = currentSyncProgress,
              albumArtistsOnly = currentAlbumArtistsOnly,
              onOpenDrawer = onOpenDrawer,
              onSearchToggle = onSearchToggle,
              onSearchQueryChange = onSearchQueryChange,
              onSearchClear = onSearchClear,
              onMenuToggle = onMenuToggle,
              onSync = onSync,
              onShowStats = onShowStats,
              onAlbumArtistsOnlyToggle = onAlbumArtistsOnlyToggle
            )
          }
        )
      )
    }
  }
}

@Composable
private fun LibraryEventsEffect(
  viewModel: LibraryViewModel,
  snackbarHostState: SnackbarHostState,
  onStatsReady: (LibraryStats) -> Unit
) {
  val networkUnavailableMessage = stringResource(R.string.connection_error_network_unavailable)

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is LibraryUiEvent.LibraryStatsReady -> onStatsReady(event.stats)

        is LibraryUiEvent.UpdateAlbumArtistOnly -> Unit

        is LibraryUiEvent.NetworkUnavailable -> snackbarHostState.showSnackbar(
          message = networkUnavailableMessage,
          duration = SnackbarDuration.Short
        )
      }
    }
  }

  SyncResultEffect(viewModel = viewModel, snackbarHostState = snackbarHostState)
}

@Composable
private fun SyncResultEffect(viewModel: LibraryViewModel, snackbarHostState: SnackbarHostState) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val syncFailedMessage = stringResource(R.string.library__sync_failed)

  LaunchedEffect(Unit) {
    viewModel.syncResults.collect { result ->
      val message = when (result) {
        is SyncResult.Success -> buildSyncCompleteMessage(context, result.stats)
        is SyncResult.Failed -> syncFailedMessage
        SyncResult.Noop -> null
      }
      if (message != null) {
        snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Long)
      }
    }
  }
}

private fun buildSyncCompleteMessage(
  context: android.content.Context,
  stats: LibraryStats
): String {
  val resources = context.resources
  return context.getString(
    R.string.library__sync_complete,
    resources.getQuantityString(R.plurals.genre, stats.genres.toInt(), stats.genres.toInt()),
    resources.getQuantityString(R.plurals.artist, stats.artists.toInt(), stats.artists.toInt()),
    resources.getQuantityString(R.plurals.album, stats.albums.toInt(), stats.albums.toInt()),
    resources.getQuantityString(R.plurals.track, stats.tracks.toInt(), stats.tracks.toInt()),
    resources.getQuantityString(
      R.plurals.playlist,
      stats.playlists.toInt(),
      stats.playlists.toInt()
    )
  )
}

@Composable
private fun LibraryContent(
  pagerState: PagerState,
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  onSync: () -> Unit,
  onTabClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxSize()) {
    PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
      LibraryTab.entries.forEachIndexed { index, tab ->
        Tab(
          selected = pagerState.currentPage == index,
          onClick = { onTabClick(index) },
          text = { Text(stringResource(tab.titleResId)) }
        )
      }
    }

    HorizontalPager(
      state = pagerState,
      beyondViewportPageCount = 1,
      modifier = Modifier.weight(1f)
    ) { page ->
      LibraryTabPage(
        tab = LibraryTab.entries[page],
        snackbarHostState = snackbarHostState,
        isSyncing = isSyncing,
        onNavigateToGenreArtists = onNavigateToGenreArtists,
        onNavigateToArtistAlbums = onNavigateToArtistAlbums,
        onNavigateToAlbumTracks = onNavigateToAlbumTracks,
        onSync = onSync
      )
    }

    MiniControl(
      onNavigateToPlayer = onNavigateToPlayer,
      snackbarHostState = snackbarHostState
    )
  }
}

@Composable
private fun LibraryTabPage(
  tab: LibraryTab,
  snackbarHostState: SnackbarHostState,
  isSyncing: Boolean,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onSync: () -> Unit
) {
  when (tab) {
    LibraryTab.GENRES -> GenresTab(
      snackbarHostState = snackbarHostState,
      isSyncing = isSyncing,
      onNavigateToGenreArtists = onNavigateToGenreArtists,
      onSync = onSync
    )

    LibraryTab.ARTISTS -> ArtistsTab(
      snackbarHostState = snackbarHostState,
      isSyncing = isSyncing,
      onNavigateToArtistAlbums = onNavigateToArtistAlbums,
      onSync = onSync
    )

    LibraryTab.ALBUMS -> AlbumsTab(
      snackbarHostState = snackbarHostState,
      isSyncing = isSyncing,
      onNavigateToAlbumTracks = onNavigateToAlbumTracks,
      onSync = onSync
    )

    LibraryTab.TRACKS -> TracksTab(
      snackbarHostState = snackbarHostState,
      isSyncing = isSyncing,
      onSync = onSync
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopBar(
  isSearchActive: Boolean,
  menuExpanded: Boolean,
  syncProgress: LibrarySyncProgress,
  albumArtistsOnly: Boolean,
  onOpenDrawer: () -> Unit,
  onSearchToggle: (Boolean) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onSearchClear: () -> Unit,
  onMenuToggle: (Boolean) -> Unit,
  onSync: () -> Unit,
  onShowStats: () -> Unit,
  onAlbumArtistsOnlyToggle: () -> Unit
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  // Manage text field state locally to avoid cursor position issues
  var localSearchText by rememberSaveable { mutableStateOf("") }

  // Clear local text when search is deactivated
  LaunchedEffect(isSearchActive) {
    if (!isSearchActive) {
      localSearchText = ""
    }
  }

  Column {
    if (isSearchActive) {
      TopAppBar(
        title = {
          TextField(
            value = localSearchText,
            onValueChange = { newValue ->
              localSearchText = newValue
              onSearchQueryChange(newValue)
            },
            placeholder = { Text(stringResource(R.string.library_search_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
          )
        },
        navigationIcon = {
          IconButton(onClick = {
            localSearchText = ""
            onSearchClear()
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(R.string.action_search_clear)
            )
          }
        },
        actions = {
          if (localSearchText.isNotEmpty()) {
            IconButton(onClick = {
              localSearchText = ""
              onSearchQueryChange("")
            }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(R.string.action_search_clear)
              )
            }
          }
        }
      )
    } else {
      RemoteTopAppBar(
        title = stringResource(R.string.common_library),
        navigationIcon = { DrawerNavigationIcon(onClick = onOpenDrawer) },
        actions = {
          IconButton(onClick = { onSearchToggle(true) }) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = stringResource(R.string.library_search_hint)
            )
          }

          IconButton(onClick = onSync) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = stringResource(R.string.action_refresh)
            )
          }

          Box {
            IconButton(onClick = { onMenuToggle(true) }) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_overflow_description)
              )
            }
            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { onMenuToggle(false) }
            ) {
              DropdownMenuItem(
                text = { Text(stringResource(R.string.library_album_artists_only)) },
                onClick = {
                  onAlbumArtistsOnlyToggle()
                },
                trailingIcon = {
                  Checkbox(
                    checked = albumArtistsOnly,
                    onCheckedChange = null
                  )
                }
              )
              DropdownMenuItem(
                text = { Text(stringResource(R.string.library_menu__sync_state)) },
                onClick = {
                  onMenuToggle(false)
                  onShowStats()
                }
              )
            }
          }
        }
      )
    }

    AnimatedVisibility(
      visible = syncProgress.running,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      SyncProgressIndicator(syncProgress = syncProgress)
    }
  }
}

@Composable
private fun SyncProgressIndicator(
  syncProgress: LibrarySyncProgress,
  modifier: Modifier = Modifier
) {
  val progress by animateFloatAsState(
    targetValue = if (syncProgress.total > 0) {
      syncProgress.current.toFloat() / syncProgress.total.toFloat()
    } else {
      0f
    },
    label = "sync_progress"
  )

  Column(modifier = modifier.fillMaxWidth()) {
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier.fillMaxWidth()
    )
    Text(
      text = stringResource(
        R.string.library__sync_progress,
        syncProgress.current,
        syncProgress.total,
        stringResource(syncProgress.category.titleRes)
      ),
      style = MaterialTheme.typography.bodySmall,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
  }
}

@Composable
private fun LibraryStatsDialog(stats: LibraryStats, onDismiss: () -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.library_stats__title)) },
    text = {
      Column {
        Text(
          text = stringResource(R.string.library_stats__description),
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier.padding(bottom = 16.dp)
        )
        StatsRow(label = stringResource(R.string.media__genres), value = stats.genres)
        StatsRow(label = stringResource(R.string.media__artists), value = stats.artists)
        StatsRow(label = stringResource(R.string.media__albums), value = stats.albums)
        StatsRow(label = stringResource(R.string.media__tracks), value = stats.tracks)
        StatsRow(label = stringResource(R.string.media__playlists), value = stats.playlists)
        StatsRow(label = stringResource(R.string.media__covers), value = stats.covers)
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(android.R.string.ok))
      }
    }
  )
}

@Composable
private fun StatsRow(label: String, value: Long) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = value.toString(),
      style = MaterialTheme.typography.bodyMedium
    )
  }
}
