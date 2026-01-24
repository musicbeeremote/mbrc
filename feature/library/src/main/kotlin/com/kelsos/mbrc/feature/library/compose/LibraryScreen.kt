package com.kelsos.mbrc.feature.library.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.common.utilities.AppError
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.ui.compose.ActionItem
import com.kelsos.mbrc.core.ui.compose.DynamicScreenScaffold
import com.kelsos.mbrc.core.ui.compose.MenuItem
import com.kelsos.mbrc.core.ui.compose.TopBarState
import com.kelsos.mbrc.feature.library.LibraryViewModel
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.compose.tabs.AlbumsTab
import com.kelsos.mbrc.feature.library.compose.tabs.ArtistsTab
import com.kelsos.mbrc.feature.library.compose.tabs.GenresTab
import com.kelsos.mbrc.feature.library.compose.tabs.TracksTab
import com.kelsos.mbrc.feature.library.data.LibraryStats
import com.kelsos.mbrc.feature.library.domain.LibrarySyncProgress
import com.kelsos.mbrc.feature.library.ui.LibraryUiEvent
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
  onOpenDrawer: () -> Unit,
  onNavigateToGenreArtists: (Genre) -> Unit,
  onNavigateToGenreAlbums: (Genre) -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: LibraryViewModel = koinViewModel()
) {
  val scope = rememberCoroutineScope()

  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var statsToShow by remember { mutableStateOf<LibraryStats?>(null) }

  val albumArtistsOnly by viewModel.albumArtistsOnly.collectAsState(initial = false)
  val syncProgress by viewModel.progress.collectAsState(initial = LibrarySyncProgress.Idle)

  val pagerState = rememberPagerState(pageCount = { LibraryTab.entries.size })

  // String resources for menu items and scaffold configuration
  val searchPlaceholder = stringResource(R.string.library_search_hint)
  val albumArtistsOnlyLabel = stringResource(R.string.library_album_artists_only)
  val syncStateLabel = stringResource(R.string.library_menu__sync_state)
  val syncProgressTemplate = stringResource(R.string.library__sync_progress)
  val libraryTitle = stringResource(R.string.common_library)
  val categoryName = if (syncProgress.running && syncProgress.category.titleRes != 0) {
    stringResource(syncProgress.category.titleRes)
  } else {
    ""
  }

  // Compute scaffold configuration based on current state
  val topBarState = when {
    isSearchActive -> TopBarState.Search(
      query = searchQuery,
      placeholder = searchPlaceholder,
      onQueryChange = { query ->
        searchQuery = query
        viewModel.search(query)
      },
      onSearch = {
        // Library search filters in real-time, no action needed on submit
      },
      onClose = {
        isSearchActive = false
        searchQuery = ""
        viewModel.search("")
      }
    )

    syncProgress.running -> {
      val progress = if (syncProgress.total > 0) {
        syncProgress.current.toFloat() / syncProgress.total.toFloat()
      } else {
        -1f // Indeterminate
      }
      TopBarState.WithProgress(
        title = libraryTitle,
        progress = progress,
        progressText = syncProgressTemplate.format(
          syncProgress.current,
          syncProgress.total,
          categoryName
        )
      )
    }

    else -> TopBarState.WithTitle(libraryTitle)
  }

  val playAllLabel = stringResource(R.string.menu_play_all)
  val shuffleAllLabel = stringResource(R.string.menu_shuffle_all)

  val menuItems = if (isSearchActive || syncProgress.running) {
    emptyList()
  } else {
    listOf(
      MenuItem(
        label = playAllLabel,
        onClick = { viewModel.playAll(shuffle = false) }
      ),
      MenuItem(
        label = shuffleAllLabel,
        onClick = { viewModel.playAll(shuffle = true) }
      ),
      MenuItem(
        label = albumArtistsOnlyLabel,
        onClick = { viewModel.updateAlbumArtistOnly(!albumArtistsOnly) },
        trailingContent = {
          Checkbox(checked = albumArtistsOnly, onCheckedChange = null)
        }
      ),
      MenuItem(
        label = syncStateLabel,
        onClick = { viewModel.displayLibraryStats() }
      )
    )
  }

  // Show search action in app bar when not searching and not syncing
  val searchDescription = stringResource(R.string.library_search_hint)
  val actionItems = if (!isSearchActive && !syncProgress.running) {
    listOf(
      ActionItem(
        icon = Icons.Default.Search,
        contentDescription = searchDescription,
        onClick = { isSearchActive = true }
      )
    )
  } else {
    emptyList()
  }

  LibraryEventsEffect(
    viewModel = viewModel,
    snackbarHostState = snackbarHostState,
    onStatsReady = { statsToShow = it }
  )

  statsToShow?.let { stats ->
    LibraryStatsDialog(stats = stats, onDismiss = { statsToShow = null })
  }

  DynamicScreenScaffold(
    topBarState = topBarState,
    snackbarHostState = snackbarHostState,
    defaultTitle = libraryTitle,
    onOpenDrawer = onOpenDrawer,
    actionItems = actionItems,
    menuItems = menuItems,
    modifier = modifier
  ) { paddingValues ->
    LibraryContent(
      pagerState = pagerState,
      snackbarHostState = snackbarHostState,
      isSyncing = syncProgress.running,
      onNavigateToGenreArtists = onNavigateToGenreArtists,
      onNavigateToGenreAlbums = onNavigateToGenreAlbums,
      onNavigateToArtistAlbums = onNavigateToArtistAlbums,
      onNavigateToAlbumTracks = onNavigateToAlbumTracks,
      onNavigateToPlayer = onNavigateToPlayer,
      onSync = { viewModel.sync() },
      onTabClick = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
      modifier = Modifier.padding(paddingValues)
    )
  }
}

@Composable
private fun LibraryEventsEffect(
  viewModel: LibraryViewModel,
  snackbarHostState: SnackbarHostState,
  onStatsReady: (LibraryStats) -> Unit
) {
  val networkUnavailableMessage = stringResource(R.string.connection_error_network_unavailable)
  val playAllSuccessMessage = stringResource(R.string.library__play_all_success)
  val playAllFailedMessage = stringResource(R.string.library__play_all_failed)

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      when (event) {
        is LibraryUiEvent.LibraryStatsReady -> onStatsReady(event.stats)

        is LibraryUiEvent.UpdateAlbumArtistOnly -> Unit

        is LibraryUiEvent.NetworkUnavailable -> snackbarHostState.showSnackbar(
          message = networkUnavailableMessage,
          duration = SnackbarDuration.Short
        )

        is LibraryUiEvent.PlayAllSuccess -> snackbarHostState.showSnackbar(
          message = playAllSuccessMessage,
          duration = SnackbarDuration.Short
        )

        is LibraryUiEvent.PlayAllFailed -> snackbarHostState.showSnackbar(
          message = playAllFailedMessage,
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
        is Outcome.Success -> buildSyncCompleteMessage(context, result.data)

        is Outcome.Failure -> when (result.error) {
          is AppError.NoOp -> null
          else -> syncFailedMessage
        }
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
  onNavigateToGenreAlbums: (Genre) -> Unit,
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
        onNavigateToGenreAlbums = onNavigateToGenreAlbums,
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
  onNavigateToGenreAlbums: (Genre) -> Unit,
  onNavigateToArtistAlbums: (Artist) -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onSync: () -> Unit
) {
  when (tab) {
    LibraryTab.GENRES -> GenresTab(
      snackbarHostState = snackbarHostState,
      isSyncing = isSyncing,
      onNavigateToGenreArtists = onNavigateToGenreArtists,
      onNavigateToGenreAlbums = onNavigateToGenreAlbums,
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
