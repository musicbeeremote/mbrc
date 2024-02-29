package com.kelsos.mbrc.features.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.ui.SingleLineRow
import com.kelsos.mbrc.common.ui.pagingDataFlow
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.features.library.presentation.GenreViewModel
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.theme.RemoteTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun GenresScreen(sync: () -> Unit, action: QueueActionWithId) {
  val vm = getViewModel<GenreViewModel>()
  GenresScreen(genres = vm.genres.collectAsLazyPagingItems(), sync = sync, action = action)
}

@Composable
fun GenresScreen(
  genres: LazyPagingItems<Genre>,
  sync: () -> Unit,
  action: QueueActionWithId
) = BrowseScreen(
  items = genres,
  text = stringResource(id = R.string.library_genres_list_empty),
  key = { it.id },
  sync = sync
) { genre ->
  SingleLineRow(
    text = genre?.genre,
    clicked = {
      genre?.let { genre ->
        action(Queue.Default, genre.id)
      }
    }
  ) {
    ActionMenu(defaultAction = R.string.menu_genre_artists) { queue ->
      genre?.let { genre ->
        action(queue, genre.id)
      }
    }
  }
}

@Preview
@Composable
fun GenresScreenPreview() {
  RemoteTheme {
    GenresScreen(
      genres = pagingDataFlow(
        Genre(
          genre = "Metal",
          id = 1
        )
      ).collectAsLazyPagingItems(),
      sync = {},
      action = { _, _ -> }
    )
  }
}
