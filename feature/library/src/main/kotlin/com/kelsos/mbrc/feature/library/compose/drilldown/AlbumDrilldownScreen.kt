package com.kelsos.mbrc.feature.library.compose.drilldown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.AlbumViewMode
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.core.ui.compose.ActionItem
import com.kelsos.mbrc.core.ui.compose.NavigationIconType
import com.kelsos.mbrc.core.ui.compose.PagingGridScreen
import com.kelsos.mbrc.core.ui.compose.PagingListScreen
import com.kelsos.mbrc.core.ui.compose.QueueResultEffect
import com.kelsos.mbrc.core.ui.compose.ScreenScaffold
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.albums.AlbumUiMessage
import com.kelsos.mbrc.feature.library.albums.FilteredAlbumViewModel
import com.kelsos.mbrc.feature.library.compose.SortBottomSheet
import com.kelsos.mbrc.feature.library.compose.SortOption
import com.kelsos.mbrc.feature.library.compose.components.AlbumGridItem
import com.kelsos.mbrc.feature.library.compose.components.AlbumListItem
import com.kelsos.mbrc.feature.library.compose.isAlbumGrid
import com.kelsos.mbrc.feature.minicontrol.MiniControl
import kotlinx.coroutines.flow.filterIsInstance

/**
 * The albums of one artist or genre, as a list or grid following the album view mode. [filter] is
 * handed to [viewModel] on entry, and [listIdPrefix] names the list for its item keys, so it must
 * stay stable across releases.
 */
@Composable
internal fun <K : Any> AlbumDrilldownScreen(
  title: String,
  filter: K,
  listIdPrefix: String,
  sortOptions: List<SortOption<AlbumSortField>>,
  viewModel: FilteredAlbumViewModel<K>,
  onNavigateBack: () -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier
) {
  val albums = viewModel.albums.collectAsLazyPagingItems()
  val sortPreference by viewModel.sortPreference.collectAsStateWithLifecycle(
    initialValue = AlbumSortPreference(AlbumSortField.NAME, SortOrder.ASC)
  )
  val albumViewMode by viewModel.albumViewMode.collectAsStateWithLifecycle(
    initialValue = AlbumViewMode.AUTO
  )
  val isGridMode = isAlbumGrid(albumViewMode)
  var showSortSheet by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(filter) {
    viewModel.load(filter)
  }

  LaunchedEffect(Unit) {
    viewModel.events.filterIsInstance<AlbumUiMessage.OpenAlbumTracks>().collect { event ->
      onNavigateToAlbumTracks(event.album)
    }
  }

  QueueResultEffect(
    queueResults = viewModel.queueResults,
    snackbarHostState = snackbarHostState
  )

  val sortDescription = stringResource(R.string.sort_button_description)
  val viewModeDescription = stringResource(R.string.album_view_mode_description)

  ScreenScaffold(
    title = title,
    snackbarHostState = snackbarHostState,
    navigationIcon = NavigationIconType.Back(onNavigateBack),
    actionItems = listOf(
      ActionItem(
        icon = if (isGridMode) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
        contentDescription = viewModeDescription,
        onClick = { viewModel.toggleViewMode() }
      ),
      ActionItem(
        icon = Icons.AutoMirrored.Filled.Sort,
        contentDescription = sortDescription,
        onClick = { showSortSheet = true }
      )
    ),
    modifier = modifier
  ) { paddingValues ->
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
      if (isGridMode) {
        PagingGridScreen(
          items = albums,
          listId = "$listIdPrefix-grid",
          modifier = Modifier.weight(1f),
          emptyMessage = stringResource(R.string.albums_list_empty),
          emptyIcon = Icons.Default.Album,
          key = { it.id }
        ) { album ->
          AlbumGridItem(
            album = album,
            onClick = { viewModel.queue(Queue.Default, album) },
            onQueue = { queue -> viewModel.queue(queue, album) }
          )
        }
      } else {
        PagingListScreen(
          items = albums,
          listId = "$listIdPrefix-list",
          modifier = Modifier.weight(1f),
          emptyMessage = stringResource(R.string.albums_list_empty),
          emptyIcon = Icons.Default.Album,
          key = { it.id }
        ) { album ->
          AlbumListItem(
            album = album,
            onClick = { viewModel.queue(Queue.Default, album) },
            onQueue = { queue -> viewModel.queue(queue, album) }
          )
        }
      }

      MiniControl(
        onNavigateToPlayer = onNavigateToPlayer,
        snackbarHostState = snackbarHostState
      )
    }
  }

  if (showSortSheet) {
    SortBottomSheet(
      title = stringResource(R.string.sort_title),
      options = sortOptions,
      selectedField = sortPreference.field,
      selectedOrder = sortPreference.order,
      onSortSelected = { field, order ->
        viewModel.updateSortPreference(AlbumSortPreference(field, order))
      },
      onDismiss = { showSortSheet = false }
    )
  }
}
