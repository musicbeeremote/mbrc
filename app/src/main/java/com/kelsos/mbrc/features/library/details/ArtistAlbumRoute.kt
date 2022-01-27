package com.kelsos.mbrc.features.library.details

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.features.library.AlbumsScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistAlbumRoute() {
  val vm = koinViewModel<ArtistAlbumViewModel>()
  AlbumsScreen(albums = vm.albums.collectAsLazyPagingItems(), sync = { /*TODO*/ }, action = {
      _,
      _,
    ->
  })
}
