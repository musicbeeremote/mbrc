package com.kelsos.mbrc.features.library.details

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.kelsos.mbrc.features.library.AlbumsScreen
import org.koin.androidx.compose.getViewModel

@Composable
fun ArtistAlbumRoute() {
  val vm = getViewModel<ArtistAlbumViewModel>()
  AlbumsScreen(albums = vm.albums.collectAsLazyPagingItems(), sync = { /*TODO*/ }, action = { _, _ -> })
}