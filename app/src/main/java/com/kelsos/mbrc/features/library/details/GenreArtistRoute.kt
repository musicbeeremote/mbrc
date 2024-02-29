package com.kelsos.mbrc.features.library.details

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.features.library.ArtistsScreen
import org.koin.androidx.compose.getViewModel

@Composable
fun GenreArtistRoute() {
  val vm = getViewModel<GenreArtistViewModel>()
  ArtistsScreen(
    artists = vm.artists.collectAsLazyPagingItems(),
    sync = {},
    action = { _, _ -> }
  )
}

