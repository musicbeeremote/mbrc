package com.kelsos.mbrc.features.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.ui.RemoteTopAppBar
import com.kelsos.mbrc.features.library.presentation.LibraryViewModel
import com.kelsos.mbrc.features.queue.Queue
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
    coroutineScope
  )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(
  openDrawer: () -> Unit,
  sync: () -> Unit,
  action: (queue: Queue, meta: Meta, id: Long) -> Unit,
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
