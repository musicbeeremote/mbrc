package com.kelsos.mbrc.features.library

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.kelsos.mbrc.theme.Accent
import com.kelsos.mbrc.theme.DarkBackground
import com.kelsos.mbrc.theme.RemoteTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun LibraryScreen(openDrawer: () -> Unit, coroutineScope: CoroutineScope) {
  val vm = getViewModel<LibraryViewModel>()
  LibraryScreen(
    openDrawer = openDrawer, sync = { vm.refresh() },
    action = { queue, meta, id ->
      vm.queue(id, meta, queue)
    },
    syncProgress = vm.syncProgress,
    coroutineScope
  )
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
      else -> throw IllegalArgumentException()
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
        color = MaterialTheme.colors.onSurface.copy(0.7f)
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(
  openDrawer: () -> Unit,
  sync: () -> Unit,
  action: (queue: Queue, meta: Meta, id: Long) -> Unit,
  syncProgress: LiveData<LibrarySyncProgress>,
  coroutineScope: CoroutineScope
) = Surface {
  val tabs = listOf(
    R.string.media__genres,
    R.string.media__artists,
    R.string.media__albums,
    R.string.media__tracks
  )

  val pagerState = rememberPagerState(pageCount = tabs.size, initialOffscreenLimit = 2)

  Column(modifier = Modifier.fillMaxSize()) {
    RemoteTopAppBar(openDrawer = openDrawer) {
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
      state = pagerState
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
