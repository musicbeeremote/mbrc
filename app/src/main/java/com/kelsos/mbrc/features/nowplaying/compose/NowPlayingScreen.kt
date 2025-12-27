package com.kelsos.mbrc.features.nowplaying.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.app.ScreenConfig
import com.kelsos.mbrc.common.state.ConnectionStatus
import com.kelsos.mbrc.common.ui.compose.DragDropState
import com.kelsos.mbrc.common.ui.compose.DrawerNavigationIcon
import com.kelsos.mbrc.common.ui.compose.EmptyScreen
import com.kelsos.mbrc.common.ui.compose.LoadingScreen
import com.kelsos.mbrc.common.ui.compose.RemoteTopAppBar
import com.kelsos.mbrc.common.ui.compose.dragContainer
import com.kelsos.mbrc.common.ui.compose.rememberDragDropState
import com.kelsos.mbrc.features.nowplaying.NowPlaying
import com.kelsos.mbrc.features.nowplaying.NowPlayingUiMessages
import com.kelsos.mbrc.features.nowplaying.NowPlayingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NowPlayingScreen(
  onOpenDrawer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  onScreenConfigChange: (ScreenConfig) -> Unit,
  viewModel: NowPlayingViewModel = koinViewModel()
) {
  val tracks = viewModel.tracks.collectAsLazyPagingItems()
  val playingTrack by viewModel.playingTrack.collectAsState()
  val connectionState by viewModel.connectionState.collectAsState(ConnectionStatus.Offline)
  val isConnected = connectionState is ConnectionStatus.Connected

  var isRefreshing by remember { mutableStateOf(false) }
  var isSearchActive by remember { mutableStateOf(false) }

  // Screen configuration
  NowPlayingScreenConfig(
    onScreenConfigChange = onScreenConfigChange,
    onOpenDrawer = onOpenDrawer,
    onSearchClick = { isSearchActive = true },
    onSearchClose = { isSearchActive = false },
    onSearch = { query ->
      viewModel.actions.search(query)
      isSearchActive = false
    },
    isSearchActive = isSearchActive
  )

  // Handle UI messages
  NowPlayingEventsEffect(
    viewModel = viewModel,
    snackbarHostState = snackbarHostState,
    onRefreshComplete = { isRefreshing = false }
  )

  NowPlayingContent(
    tracks = tracks,
    playingTrackPath = playingTrack.path,
    isRefreshing = isRefreshing,
    isConnected = isConnected,
    onRefresh = {
      isRefreshing = true
      viewModel.actions.reload()
    },
    onTrackClick = { position -> viewModel.actions.play(position) },
    onTrackRemove = { position -> viewModel.actions.removeTrack(position) },
    onTrackMove = { from, to -> viewModel.actions.moveTrack(from, to) },
    onDragEnd = { viewModel.actions.move() }
  )
}

@Composable
private fun NowPlayingScreenConfig(
  onScreenConfigChange: (ScreenConfig) -> Unit,
  onOpenDrawer: () -> Unit,
  onSearchClick: () -> Unit,
  onSearchClose: () -> Unit,
  onSearch: (String) -> Unit,
  isSearchActive: Boolean
) {
  val title = stringResource(R.string.nav_now_playing)

  // Unit ensures this runs on first composition (navigation to screen)
  LaunchedEffect(Unit, isSearchActive) {
    onScreenConfigChange(
      ScreenConfig(
        topBar = {
          if (isSearchActive) {
            NowPlayingSearchBar(
              onSearchClose = onSearchClose,
              onSearch = onSearch
            )
          } else {
            NowPlayingTopBar(
              title = title,
              onOpenDrawer = onOpenDrawer,
              onSearchClick = onSearchClick
            )
          }
        }
      )
    )
  }
}

@Composable
private fun NowPlayingTopBar(title: String, onOpenDrawer: () -> Unit, onSearchClick: () -> Unit) {
  RemoteTopAppBar(
    title = title,
    navigationIcon = { DrawerNavigationIcon(onClick = onOpenDrawer) },
    actions = {
      IconButton(onClick = onSearchClick) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = stringResource(R.string.now_playing_search_hint)
        )
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NowPlayingSearchBar(onSearchClose: () -> Unit, onSearch: (String) -> Unit) {
  val keyboardController = LocalSoftwareKeyboardController.current
  // Manage text field state locally to avoid cursor position issues
  var localSearchText by remember { mutableStateOf("") }

  TopAppBar(
    title = {
      TextField(
        value = localSearchText,
        onValueChange = { localSearchText = it },
        placeholder = { Text(stringResource(R.string.now_playing_search_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
          onSearch = {
            if (localSearchText.isNotBlank()) {
              keyboardController?.hide()
              onSearch(localSearchText)
            }
          }
        ),
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
        onSearchClose()
      }) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(R.string.action_search_clear)
        )
      }
    },
    actions = {
      if (localSearchText.isNotEmpty()) {
        IconButton(onClick = { localSearchText = "" }) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = stringResource(R.string.action_search_clear)
          )
        }
      }
    }
  )
}

@Composable
private fun NowPlayingEventsEffect(
  viewModel: NowPlayingViewModel,
  snackbarHostState: SnackbarHostState,
  onRefreshComplete: () -> Unit
) {
  val refreshFailedMessage = stringResource(R.string.refresh_failed)
  val networkUnavailableMessage = stringResource(R.string.connection_error_network_unavailable)
  val playFailedMessage = stringResource(R.string.radio__play_failed)
  val searchSuccessTemplate = stringResource(R.string.now_playing__search_success)
  val searchNotFoundMessage = stringResource(R.string.now_playing__search_not_found)

  LaunchedEffect(Unit) {
    viewModel.events.collect { message ->
      when (message) {
        is NowPlayingUiMessages.RefreshFailed -> {
          onRefreshComplete()
          snackbarHostState.showSnackbar(refreshFailedMessage)
        }

        NowPlayingUiMessages.RefreshSucceeded -> {
          onRefreshComplete()
        }

        NowPlayingUiMessages.NetworkUnavailable -> {
          onRefreshComplete()
          snackbarHostState.showSnackbar(networkUnavailableMessage)
        }

        NowPlayingUiMessages.PlayFailed -> {
          snackbarHostState.showSnackbar(playFailedMessage)
        }

        NowPlayingUiMessages.RemoveFailed -> {
          snackbarHostState.showSnackbar(refreshFailedMessage)
        }

        NowPlayingUiMessages.MoveFailed -> {
          snackbarHostState.showSnackbar(refreshFailedMessage)
        }

        is NowPlayingUiMessages.SearchSuccess -> {
          snackbarHostState.showSnackbar(searchSuccessTemplate.format(message.trackTitle))
        }

        NowPlayingUiMessages.SearchNotFound -> {
          snackbarHostState.showSnackbar(searchNotFoundMessage)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NowPlayingContent(
  tracks: LazyPagingItems<NowPlaying>,
  playingTrackPath: String,
  isRefreshing: Boolean,
  isConnected: Boolean,
  onRefresh: () -> Unit,
  onTrackClick: (Int) -> Unit,
  onTrackRemove: (Int) -> Unit,
  onTrackMove: (Int, Int) -> Unit,
  onDragEnd: () -> Unit
) {
  PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = onRefresh,
    modifier = Modifier.fillMaxSize()
  ) {
    when (val refreshState = tracks.loadState.refresh) {
      is LoadState.Loading if tracks.itemCount == 0 -> {
        LoadingScreen()
      }

      is LoadState.Error if tracks.itemCount == 0 -> {
        EmptyScreen(
          message = refreshState.error.message ?: stringResource(R.string.refresh_failed),
          icon = Icons.AutoMirrored.Filled.QueueMusic
        )
      }

      is LoadState.NotLoading if tracks.itemCount == 0 -> {
        EmptyScreen(
          message = stringResource(R.string.now_playing__empty_list),
          icon = Icons.AutoMirrored.Filled.QueueMusic
        )
      }

      else -> {
        NowPlayingTrackList(
          tracks = tracks,
          playingTrackPath = playingTrackPath,
          isConnected = isConnected,
          onTrackClick = onTrackClick,
          onTrackRemove = onTrackRemove,
          onTrackMove = onTrackMove,
          onDragEnd = onDragEnd
        )
      }
    }
  }
}

@Composable
private fun NowPlayingTrackList(
  tracks: LazyPagingItems<NowPlaying>,
  playingTrackPath: String,
  isConnected: Boolean,
  onTrackClick: (Int) -> Unit,
  onTrackRemove: (Int) -> Unit,
  onTrackMove: (Int, Int) -> Unit,
  onDragEnd: () -> Unit
) {
  val lazyListState = rememberLazyListState()

  // Create a mutable list from paging items that can be reordered during drag
  val trackList = remember { mutableListOf<NowPlaying>() }

  // Sync the mutable list with paging items when they change
  LaunchedEffect(tracks.itemCount) {
    trackList.clear()
    for (i in 0 until tracks.itemCount) {
      tracks[i]?.let { trackList.add(it) }
    }
  }

  // Use a snapshot state list for Compose to observe changes
  val draggableList = remember { trackList.toMutableStateList() }

  // Keep draggable list in sync with track list when not dragging
  LaunchedEffect(tracks.itemCount, tracks.itemSnapshotList) {
    if (draggableList.size != tracks.itemCount) {
      draggableList.clear()
      for (i in 0 until tracks.itemCount) {
        tracks[i]?.let { draggableList.add(it) }
      }
    }
  }

  val dragDropState = rememberDragDropState(
    lazyListState = lazyListState,
    onMove = { from, to ->
      // Actually reorder the list like the demo does
      draggableList.add(to, draggableList.removeAt(from))
      // Also notify the move manager
      onTrackMove(from, to)
    },
    onDragEnd = onDragEnd
  )

  // Only enable drag if connected
  val dragModifier = if (isConnected) {
    Modifier.dragContainer(dragDropState)
  } else {
    Modifier
  }

  LazyColumn(
    state = lazyListState,
    modifier = Modifier
      .fillMaxSize()
      .then(dragModifier),
    flingBehavior = ScrollableDefaults.flingBehavior()
  ) {
    itemsIndexed(
      items = draggableList,
      key = { _, track -> track.id }
    ) { index, track ->
      val isDragging = index == dragDropState.draggingItemIndex
      val isPlaying = track.path == playingTrackPath

      if (isConnected) {
        SwipeableNowPlayingItem(
          track = track,
          index = index,
          isPlaying = isPlaying,
          isDragging = isDragging,
          dragDropState = dragDropState,
          onClick = { onTrackClick(index + 1) },
          onRemove = { onTrackRemove(index) }
        )
      } else {
        NowPlayingTrackItem(
          track = track,
          isPlaying = isPlaying,
          isDragging = false,
          onClick = { onTrackClick(index + 1) },
          modifier = Modifier.animateItem()
        )
      }
    }

    if (tracks.loadState.append is LoadState.Loading) {
      item(contentType = "loading") {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyItemScope.SwipeableNowPlayingItem(
  track: NowPlaying,
  index: Int,
  isPlaying: Boolean,
  isDragging: Boolean,
  dragDropState: DragDropState,
  onClick: () -> Unit,
  onRemove: () -> Unit
) {
  val dismissState = rememberSwipeToDismissBoxState(
    SwipeToDismissBoxValue.Settled,
    SwipeToDismissBoxDefaults.positionalThreshold
  )

  // Apply drag visual effects
  val draggingModifier = when {
    isDragging -> {
      Modifier
        .zIndex(1f)
        .graphicsLayer { translationY = dragDropState.draggingItemOffset }
    }

    index == dragDropState.previousIndexOfDraggedItem -> {
      Modifier
        .zIndex(1f)
        .graphicsLayer { translationY = dragDropState.previousItemOffset.value }
    }

    else -> {
      Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
    }
  }

  SwipeToDismissBox(
    state = dismissState,
    modifier = draggingModifier,
    backgroundContent = {
      val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
          SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surface

          SwipeToDismissBoxValue.StartToEnd,
          SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
        },
        label = "swipe_background"
      )

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 16.dp),
        contentAlignment = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
          Alignment.CenterStart
        } else {
          Alignment.CenterEnd
        }
      ) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = stringResource(R.string.menu_remove_track),
          tint = MaterialTheme.colorScheme.onErrorContainer
        )
      }
    },
    enableDismissFromStartToEnd = true,
    enableDismissFromEndToStart = true,
    onDismiss = { onRemove() }
  ) {
    NowPlayingTrackItem(
      track = track,
      isPlaying = isPlaying,
      isDragging = isDragging,
      onClick = onClick
    )
  }
}

@Composable
internal fun NowPlayingTrackItem(
  track: NowPlaying,
  isPlaying: Boolean,
  isDragging: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val elevation by animateDpAsState(
    targetValue = if (isDragging) 8.dp else 0.dp,
    label = "drag_elevation"
  )

  val backgroundColor = if (isPlaying) {
    MaterialTheme.colorScheme.surfaceContainerHighest
  } else {
    MaterialTheme.colorScheme.surface
  }

  Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    shape = RoundedCornerShape(0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
    ) {
      // Left accent bar - always reserve space, colored when playing
      Box(
        modifier = Modifier
          .width(4.dp)
          .fillMaxHeight()
          .background(
            if (isPlaying) MaterialTheme.colorScheme.primary else Color.Transparent
          )
      )

      Row(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Playing indicator
        if (isPlaying) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = stringResource(R.string.nav_now_playing),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Track info
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = track.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = track.artist,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Drag handle
        Icon(
          imageVector = Icons.Default.DragIndicator,
          contentDescription = stringResource(R.string.now_playing__drag_handle),
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(24.dp)
        )
      }
    }
  }
}
