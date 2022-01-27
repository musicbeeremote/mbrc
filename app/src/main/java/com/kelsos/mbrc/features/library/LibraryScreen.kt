package com.kelsos.mbrc.features.library

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.library.presentation.LibraryViewModel
import com.kelsos.mbrc.features.library.sync.LibrarySyncProgress
import com.kelsos.mbrc.features.library.sync.SyncCategory
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.DarkBackground
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun LibraryScreen(
  openDrawer: () -> Unit,
  coroutineScope: CoroutineScope,
  vm: LibraryViewModel = getViewModel()
) {
  LibraryScreen(
    openDrawer = openDrawer, sync = { vm.refresh() },
    action = { queue, meta, id ->
      vm.queue(id, meta, queue)
    },
    syncProgress = vm.syncProgress,
    albumArtistsOnly = vm.albumArtistOnly,
    setAlbumArtistOnly = { vm.setAlbumArtistOnly(it) },
    metrics = vm.syncState,
    coroutineScope
  )
}

@Composable
fun SyncMetricsDialog(metrics: Flow<SyncedData>, showDialog: Boolean, dismiss: () -> Unit) {
  if (showDialog) {
    Dialog(
      onDismissRequest = { dismiss() },
    ) {
      Surface(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        val syncedData by metrics.collectAsState(initial = SyncedData(0, 0, 0, 0, 0))
        SyncMetricsContent(syncedData, dismiss)
      }
    }
  }
}

@Composable
fun SyncMetricsContent(metrics: SyncedData, dismiss: () -> Unit) =
  Column(modifier = Modifier.padding(16.dp)) {
    Row(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = stringResource(id = R.string.library_stats__title),
        style = MaterialTheme.typography.h6
      )
    }
    Row(
      modifier = Modifier
        .padding(vertical = 16.dp)
        .fillMaxWidth()
    ) {
      Text(
        text = stringResource(id = R.string.library_stats__description),
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
      )
    }

    MetricsRow(R.string.media__genres, metrics.genres)
    MetricsRow(R.string.media__artists, metrics.artists)
    MetricsRow(R.string.media__albums, metrics.albums)
    MetricsRow(R.string.media__tracks, metrics.tracks)
    MetricsRow(R.string.media__playlists, metrics.playlists)
    Row(modifier = Modifier.padding(top = 16.dp)) {
      Spacer(modifier = Modifier.weight(1f))
      TextButton(onClick = dismiss) {
        Text(text = stringResource(id = android.R.string.ok))
      }
    }
  }

@Composable
private fun MetricsRow(@StringRes resId: Int, items: Long) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Column {
      Text(text = stringResource(id = resId), color = MaterialTheme.colors.primary)
    }
    Column {
      Text(text = items.toString())
    }
  }
}

@Preview
@Composable
fun SyncMetricsContentPreview() {
  SyncMetricsContent(metrics = SyncedData(10, 40, 100, 1000, 4)) {}
}

@Composable
fun LibrarySyncIndicator(syncProgress: LiveData<LibrarySyncProgress>) {
  val progress by syncProgress.observeAsState(initial = LibrarySyncProgress(0, 0, 0, false))
  LibrarySyncIndicator(syncProgress = progress)
}

@Composable
fun LibrarySyncIndicator(syncProgress: LibrarySyncProgress) = Surface(
  color = DarkBackground
) {
  if (!syncProgress.running) {
    return@Surface
  }
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(26.dp)
  ) {
    val category = when (syncProgress.category) {
      SyncCategory.GENRES -> R.string.library__category_genres
      SyncCategory.ARTISTS -> R.string.library__category_artists
      SyncCategory.ALBUMS -> R.string.library__category_albums
      SyncCategory.TRACKS -> R.string.library__category_tracks
      SyncCategory.PLAYLISTS -> R.string.library__category_playlists
      SyncCategory.COVERS -> R.string.library__category_covers
      else -> R.string.media__genres
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = stringResource(
          id = R.string.library_container__sync_text,
          syncProgress.current,
          syncProgress.total,
          stringResource(id = category)
        ),
        style = MaterialTheme.typography.subtitle2,
        modifier = Modifier.weight(1f, true),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
      )
      val currentProgress = syncProgress.current.toFloat().div(syncProgress.total)
      val animatedProgress = animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
      ).value
      LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier.fillMaxWidth(),
        color = Accent
      )
    }
  }
}

@Preview
@Composable
fun LibrarySyncIndicatorPreview() {
  LibrarySyncIndicator(syncProgress = LibrarySyncProgress(12000, 120000, 2, true))
}

@Composable
fun SyncButton(sync: () -> Unit) {
  IconButton(onClick = { sync() }) {
    Icon(
      imageVector = Icons.Filled.Refresh,
      contentDescription = stringResource(id = R.string.press_to_sync)
    )
  }
}

@Composable
fun LibraryScreenOverflow(
  showAlbumArtistsOnly: Flow<Boolean>,
  setAlbumArtistOnly: (enabled: Boolean) -> Unit,
  metrics: Flow<SyncedData>
) {
  var showMenu by remember { mutableStateOf(false) }
  var showMetricsDialog by remember { mutableStateOf(false) }
  IconButton(onClick = { showMenu = !showMenu }) {
    Icon(
      imageVector = Icons.Filled.MoreVert,
      contentDescription = stringResource(id = R.string.press_to_sync)
    )
  }
  DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
    val albumArtistsOnly by showAlbumArtistsOnly.collectAsState(initial = false)
    DropdownMenuItem(onClick = { setAlbumArtistOnly(!albumArtistsOnly) }) {
      Checkbox(checked = albumArtistsOnly, onCheckedChange = setAlbumArtistOnly)
      Text(
        text = stringResource(id = R.string.library__action_only_album_artists),
        modifier = Modifier.padding(start = 16.dp)
      )
    }

    DropdownMenuItem(onClick = {
      showMenu = false
      showMetricsDialog = true
    }) {
      Text(
        text = stringResource(id = R.string.library_menu__sync_state),
      )
    }
  }
  SyncMetricsDialog(metrics, showDialog = showMetricsDialog) { showMetricsDialog = false }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(
  openDrawer: () -> Unit,
  sync: () -> Unit,
  action: (queue: Queue, meta: Meta, id: Long) -> Unit,
  syncProgress: LiveData<LibrarySyncProgress>,
  albumArtistsOnly: Flow<Boolean>,
  setAlbumArtistOnly: (enabled: Boolean) -> Unit,
  metrics: Flow<SyncedData>,
  coroutineScope: CoroutineScope
) = Surface {
  val tabs = listOf(
    R.string.media__genres,
    R.string.media__artists,
    R.string.media__albums,
    R.string.media__tracks
  )

  val pagerState = rememberPagerState()

  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {
      Row {
        IconButton(onClick = { /*TODO*/ }) {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.library_search_hint)
          )
        }
        SyncButton(sync = sync)
        LibraryScreenOverflow(albumArtistsOnly, setAlbumArtistOnly, metrics)
      }
    }
    TabRow(
      selectedTabIndex = pagerState.currentPage,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
      }
    ) {
      tabs.forEachIndexed { index, titleId ->
        Tab(
          text = { Text(text = stringResource(id = titleId)) },
          selected = pagerState.currentPage == index,
          onClick = {
            coroutineScope.launch {
              pagerState.scrollToPage(index)
            }
          }
        )
      }
    }
    LibrarySyncIndicator(syncProgress = syncProgress)
    HorizontalPager(
      modifier = Modifier.weight(1f),
      state = pagerState,
      count = tabs.size
    ) { page ->
      when (page) {
        Pages.GENRES -> GenresScreen(sync = sync) { queue, id ->
          action(queue, Meta.Genre, id)
        }
        Pages.ARTISTS -> ArtistsScreen(sync = sync) { queue, id ->
          action(queue, Meta.Artist, id)
        }
        Pages.ALBUMS -> AlbumsScreen(sync = sync) { queue, id ->
          action(queue, Meta.Album, id)
        }
        Pages.TRACKS -> TracksScreen(sync = sync) { queue, id ->
          action(queue, Meta.Track, id)
        }
      }
    }
  }
}

object Pages {
  const val GENRES = 0
  const val ARTISTS = 1
  const val ALBUMS = 2
  const val TRACKS = 3
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun LibraryScreenPreview() {
  RemoteTheme {
    LibraryScreen(openDrawer = {}, coroutineScope = MainScope())
  }
}
