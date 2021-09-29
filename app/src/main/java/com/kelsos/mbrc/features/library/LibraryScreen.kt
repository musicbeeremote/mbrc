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
import org.koin.androidx.compose.getViewModel

@Composable
fun LibraryScreen(openDrawer: () -> Unit) {
  val vm = getViewModel<LibraryViewModel>()
  LibraryScreen(
    openDrawer = openDrawer, sync = { vm.refresh() },
    action = { queue, meta, id ->
      vm.queue(id, meta, queue)
    }
  )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(
  openDrawer: () -> Unit,
  sync: () -> Unit,
  action: (queue: Queue, meta: Meta, id: Long) -> Unit
) = Surface {
  val tabs = listOf(
    R.string.media__genres,
    R.string.media__artists,
    R.string.media__albums,
    R.string.media__tracks
  )

  val pagerState = rememberPagerState(pageCount = tabs.size)

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
          onClick = {}
        )
      }
    }
    HorizontalPager(
      modifier = Modifier.weight(1f),
      state = pagerState
    ) {
      when (pagerState.currentPage) {
        0 -> GenresScreen(sync = sync) { queue, id ->
          action(queue, Meta.Genre, id)
        }
        1 -> ArtistsScreen(sync = sync) { queue, id ->
          action(queue, Meta.Artist, id)
        }
        2 -> AlbumsScreen(sync = sync) { queue, id ->
          action(queue, Meta.Album, id)
        }
        3 -> TracksScreen(sync = sync) { queue, id ->
          action(queue, Meta.Track, id)
        }
      }
    }
  }
}

@Preview(device = Devices.PIXEL_4)
@Composable
fun LibraryScreenPreview() {
  RemoteTheme {
    LibraryScreen(openDrawer = {})
  }
}
