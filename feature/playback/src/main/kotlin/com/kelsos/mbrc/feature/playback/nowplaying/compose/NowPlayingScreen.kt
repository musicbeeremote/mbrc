package com.kelsos.mbrc.feature.playback.nowplaying.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.data.nowplaying.NowPlaying
import com.kelsos.mbrc.core.ui.compose.ActionItem
import com.kelsos.mbrc.core.ui.compose.AudioBarsIndicator
import com.kelsos.mbrc.core.ui.compose.DragDropState
import com.kelsos.mbrc.core.ui.compose.DynamicScreenScaffold
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.compose.LoadingScreen
import com.kelsos.mbrc.core.ui.compose.TopBarState
import com.kelsos.mbrc.core.ui.compose.displayedSourceIndex
import com.kelsos.mbrc.core.ui.compose.dragContainer
import com.kelsos.mbrc.core.ui.compose.rememberDragDropState
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import com.kelsos.mbrc.feature.playback.R
import com.kelsos.mbrc.feature.playback.nowplaying.NowPlayingUiMessages
import com.kelsos.mbrc.feature.playback.nowplaying.NowPlayingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NowPlayingScreen(
  onOpenDrawer: () -> Unit,
  onNavigateToPlayer: () -> Unit,
  onNavigateToAlbum: ((path: String) -> Unit)?,
  onNavigateToArtist: (artist: String) -> Unit,
  snackbarHostState: SnackbarHostState,
  viewModel: NowPlayingViewModel = koinViewModel()
) {
  val tracks = viewModel.tracks.collectAsLazyPagingItems()
  val playingTrack by viewModel.playingTrack.collectAsStateWithLifecycle()
  val connectionState by viewModel.connectionState.collectAsStateWithLifecycle(
    initialValue = ConnectionStatus.Offline
  )
  val isConnected = connectionState is ConnectionStatus.Connected
  val trackCount by viewModel.trackCount.collectAsStateWithLifecycle()

  var isRefreshing by remember { mutableStateOf(false) }
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var searchQuery by rememberSaveable { mutableStateOf("") }

  val searchPlaceholder = stringResource(R.string.now_playing_search_hint)
  val title = stringResource(R.string.nav_queue)

  // Compute scaffold configuration based on current state
  val topBarState = if (isSearchActive) {
    TopBarState.Search(
      query = searchQuery,
      placeholder = searchPlaceholder,
      onQueryChange = { searchQuery = it },
      onSearch = {
        if (searchQuery.isNotBlank()) {
          viewModel.actions.search(searchQuery)
        }
      },
      onClose = {
        isSearchActive = false
        searchQuery = ""
      }
    )
  } else {
    TopBarState.WithTitle(title)
  }

  // Show search action in app bar when not searching
  val actionItems = if (!isSearchActive) {
    listOf(
      ActionItem(
        icon = Icons.Default.Search,
        contentDescription = searchPlaceholder,
        onClick = { isSearchActive = true }
      )
    )
  } else {
    emptyList()
  }

  // Handle UI messages
  NowPlayingEventsEffect(
    viewModel = viewModel,
    snackbarHostState = snackbarHostState,
    onRefreshComplete = { isRefreshing = false },
    onSearchComplete = {
      isSearchActive = false
      searchQuery = ""
    }
  )

  DynamicScreenScaffold(
    topBarState = topBarState,
    snackbarHostState = snackbarHostState,
    defaultTitle = title,
    onOpenDrawer = onOpenDrawer,
    actionItems = actionItems
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      NowPlayingContent(
        tracks = tracks,
        playingTrackPath = playingTrack.path,
        trackCount = trackCount,
        isRefreshing = isRefreshing,
        isConnected = isConnected,
        onRefresh = {
          isRefreshing = true
          viewModel.actions.reload()
        },
        onTrackClick = { position -> viewModel.actions.play(position) },
        onTrackRemove = { position -> viewModel.actions.removeTrack(position) },
        onTrackMove = { from, to -> viewModel.actions.moveTrack(from, to) },
        onDragEnd = { viewModel.actions.move() },
        onGoToAlbum = onNavigateToAlbum,
        onGoToArtist = onNavigateToArtist,
        modifier = Modifier.weight(1f)
      )

      MiniControl(
        onNavigateToPlayer = onNavigateToPlayer,
        snackbarHostState = snackbarHostState
      )
    }
  }
}

@Composable
private fun NowPlayingEventsEffect(
  viewModel: NowPlayingViewModel,
  snackbarHostState: SnackbarHostState,
  onRefreshComplete: () -> Unit,
  onSearchComplete: () -> Unit
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
          onSearchComplete()
          snackbarHostState.showSnackbar(searchSuccessTemplate.format(message.trackTitle))
        }

        NowPlayingUiMessages.SearchNotFound -> {
          onSearchComplete()
          snackbarHostState.showSnackbar(searchNotFoundMessage)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NowPlayingContent(
  tracks: LazyPagingItems<NowPlaying>,
  playingTrackPath: String,
  trackCount: Int,
  isRefreshing: Boolean,
  isConnected: Boolean,
  onRefresh: () -> Unit,
  onTrackClick: (Int) -> Unit,
  onTrackRemove: (Int) -> Unit,
  onTrackMove: (Int, Int) -> Unit,
  onDragEnd: () -> Unit,
  onGoToAlbum: ((path: String) -> Unit)?,
  onGoToArtist: (artist: String) -> Unit,
  modifier: Modifier = Modifier,
  lazyListState: LazyListState = rememberLazyListState()
) {
  val dragDropState = rememberDragDropState(
    lazyListState = lazyListState,
    onMove = { from, to -> onTrackMove(from, to) },
    onDragEnd = onDragEnd
  )

  PullToRefreshBox(
    isRefreshing = isRefreshing,
    onRefresh = onRefresh,
    modifier = modifier.fillMaxSize()
  ) {
    when (val refreshState = tracks.loadState.refresh) {
      is LoadState.Loading if tracks.itemCount == 0 -> {
        ScrollablePlaceholder { LoadingScreen() }
      }

      is LoadState.Error if tracks.itemCount == 0 -> {
        ScrollablePlaceholder {
          EmptyScreen(
            message = refreshState.error.message ?: stringResource(R.string.refresh_failed),
            icon = Icons.AutoMirrored.Filled.QueueMusic
          )
        }
      }

      is LoadState.NotLoading if tracks.itemCount == 0 -> {
        ScrollablePlaceholder {
          EmptyScreen(
            message = stringResource(R.string.now_playing__empty_list),
            icon = Icons.AutoMirrored.Filled.QueueMusic
          )
        }
      }

      else -> {
        Column(modifier = Modifier.fillMaxSize()) {
          if (trackCount > 0) {
            QueueHeader(trackCount = trackCount)
          }
          NowPlayingTrackList(
            lazyListState = lazyListState,
            dragDropState = dragDropState,
            tracks = tracks,
            playingTrackPath = playingTrackPath,
            isConnected = isConnected,
            onTrackClick = onTrackClick,
            onTrackRemove = onTrackRemove,
            onGoToAlbum = onGoToAlbum,
            onGoToArtist = onGoToArtist,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

@Composable
private fun ScrollablePlaceholder(content: @Composable BoxScope.() -> Unit) {
  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = maxHeight)
        .verticalScroll(rememberScrollState()),
      contentAlignment = Alignment.Center,
      content = content
    )
  }
}

@Composable
private fun QueueHeader(trackCount: Int) {
  Text(
    text = pluralStringResource(R.plurals.now_playing__track_count, trackCount, trackCount),
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.End,
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      .padding(horizontal = 16.dp, vertical = 8.dp)
  )
}

@Composable
private fun NowPlayingTrackList(
  lazyListState: LazyListState,
  dragDropState: DragDropState,
  tracks: LazyPagingItems<NowPlaying>,
  playingTrackPath: String,
  isConnected: Boolean,
  onTrackClick: (Int) -> Unit,
  onTrackRemove: (Int) -> Unit,
  onGoToAlbum: ((path: String) -> Unit)?,
  onGoToArtist: (artist: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val dragModifier = remember(isConnected, dragDropState) {
    if (isConnected) Modifier.dragContainer(dragDropState) else Modifier
  }

  // Captured once when the drag starts so we can detect — by id match at
  // `target` — when paging has emitted the reordered window, and stop
  // permuting before the dragged item snaps back.
  val draggedId = remember(dragDropState.dragSourceIndex) {
    dragDropState.dragSourceIndex?.let { tracks.peek(it)?.id }
  }

  // Iterates `tracks` directly so Paging anchors its next refresh around the
  // user's actual scroll position. `enablePlaceholders=true` on the pager
  // keeps `itemCount` stable across `dao.move()` invalidations, preventing
  // the snap-to-page-boundary that broke consecutive drags past ~position 100.
  LazyColumn(
    state = lazyListState,
    modifier = modifier
      .fillMaxSize()
      .then(dragModifier),
    flingBehavior = ScrollableDefaults.flingBehavior()
  ) {
    items(
      count = tracks.itemCount,
      key = { slot ->
        val srcIdx = sourceIndexFor(slot, dragDropState, tracks, draggedId)
        tracks.peek(srcIdx)?.id ?: "${ContentTypes.PLACEHOLDER}-$srcIdx"
      },
      contentType = { slot ->
        val srcIdx = sourceIndexFor(slot, dragDropState, tracks, draggedId)
        when {
          tracks.peek(srcIdx) == null -> ContentTypes.PLACEHOLDER
          isConnected -> ContentTypes.TRACK_SWIPEABLE
          else -> ContentTypes.TRACK_PLAIN
        }
      }
    ) { slot ->
      val srcIdx = sourceIndexFor(slot, dragDropState, tracks, draggedId)
      val track = tracks[srcIdx]
      val isDragging = slot == dragDropState.draggingItemIndex
      val isPlaying = track?.path == playingTrackPath

      if (track == null) {
        NowPlayingPlaceholderItem()
      } else if (isConnected) {
        SwipeableNowPlayingItem(
          track = track,
          index = srcIdx,
          isPlaying = isPlaying,
          isDragging = isDragging,
          dragDropState = dragDropState,
          onClick = { onTrackClick(srcIdx + 1) },
          onRemove = { onTrackRemove(srcIdx) },
          onGoToAlbum = onGoToAlbum?.let { callback -> { callback(track.path) } },
          onGoToArtist = { onGoToArtist(track.artist) }
        )
      } else {
        NowPlayingTrackItem(
          track = track,
          isPlaying = isPlaying,
          isDragging = false,
          onClick = { onTrackClick(srcIdx + 1) },
          onGoToAlbum = onGoToAlbum?.let { callback -> { callback(track.path) } },
          onGoToArtist = { onGoToArtist(track.artist) },
          modifier = Modifier.animateItem()
        )
      }
    }

    if (tracks.loadState.append is LoadState.Loading) {
      item(contentType = ContentTypes.LOADING) {
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

private object ContentTypes {
  const val PLACEHOLDER = "placeholder"
  const val TRACK_SWIPEABLE = "track-swipeable"
  const val TRACK_PLAIN = "track-plain"
  const val LOADING = "loading"
}

private fun sourceIndexFor(
  slot: Int,
  dragDropState: DragDropState,
  tracks: LazyPagingItems<NowPlaying>,
  draggedId: Long?
): Int = resolveSourceIndex(
  slot = slot,
  source = dragDropState.dragSourceIndex,
  target = dragDropState.draggingItemIndex,
  isSettling = dragDropState.isSettling,
  draggedId = draggedId,
  idAtTarget = { tgt -> tracks.peek(tgt)?.id }
)

/**
 * Computes the source-list index a LazyColumn slot should render. Applies
 * the in-flight drag permutation, except once paging has emitted the
 * reordered data after a drop — detected by [idAtTarget] returning the id
 * captured at drag-start. From that point until [DragDropState] clears its
 * drag indices, slots map directly (no permutation), so neighbor keys stay
 * stable and `animateItem` doesn't fire spurious placement animations.
 */
internal fun resolveSourceIndex(
  slot: Int,
  source: Int?,
  target: Int?,
  isSettling: Boolean,
  draggedId: Long?,
  idAtTarget: (Int) -> Long?
): Int {
  if (source == null || target == null) return slot
  val pagingHasNewOrder = isSettling &&
    draggedId != null &&
    idAtTarget(target) == draggedId
  if (pagingHasNewOrder) return slot
  return displayedSourceIndex(slot = slot, source = source, target = target)
}

@Composable
private fun NowPlayingPlaceholderItem() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(MaterialTheme.colorScheme.surface)
  )
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
  onRemove: () -> Unit,
  onGoToAlbum: (() -> Unit)?,
  onGoToArtist: () -> Unit
) {
  val dismissState = rememberSwipeToDismissBoxState(
    initialValue = SwipeToDismissBoxValue.Settled,
    positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
  )

  // Apply drag visual effects
  val draggingModifier = if (isDragging) {
    Modifier
      .zIndex(1f)
      .graphicsLayer { translationY = dragDropState.draggingItemOffset }
  } else {
    Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
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
      onClick = onClick,
      onGoToAlbum = onGoToAlbum,
      onGoToArtist = onGoToArtist
    )
  }
}

@Composable
fun NowPlayingTrackItem(
  track: NowPlaying,
  isPlaying: Boolean,
  isDragging: Boolean,
  onClick: () -> Unit,
  onGoToAlbum: (() -> Unit)?,
  onGoToArtist: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

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
          .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Playing indicator - animated audio bars
        if (isPlaying) {
          AudioBarsIndicator(
            color = MaterialTheme.colorScheme.primary,
            barMaxHeight = 18.dp,
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

        // Overflow menu
        Box {
          IconButton(onClick = { showMenu = true }) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = stringResource(R.string.menu_overflow_description),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            if (onGoToAlbum != null) {
              DropdownMenuItem(
                text = { Text(stringResource(R.string.player_go_to_album)) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null
                  )
                },
                onClick = {
                  showMenu = false
                  onGoToAlbum()
                }
              )
            }
            DropdownMenuItem(
              text = { Text(stringResource(R.string.player_go_to_artist)) },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null
                )
              },
              onClick = {
                showMenu = false
                onGoToArtist()
              }
            )
          }
        }

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
