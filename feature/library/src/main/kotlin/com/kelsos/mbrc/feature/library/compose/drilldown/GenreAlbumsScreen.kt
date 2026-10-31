package com.kelsos.mbrc.feature.library.compose.drilldown

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.feature.library.albums.GenreAlbumsViewModel
import com.kelsos.mbrc.feature.library.compose.tabs.albumSortOptions
import org.koin.androidx.compose.koinViewModel

@Composable
fun GenreAlbumsScreen(
  genreId: Long,
  genreName: String,
  onNavigateBack: () -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: GenreAlbumsViewModel = koinViewModel()
) {
  AlbumDrilldownScreen(
    title = genreName,
    filter = genreId,
    listIdPrefix = "genrealbums",
    sortOptions = albumSortOptions,
    viewModel = viewModel,
    onNavigateBack = onNavigateBack,
    onNavigateToAlbumTracks = onNavigateToAlbumTracks,
    onNavigateToPlayer = onNavigateToPlayer,
    snackbarHostState = snackbarHostState,
    modifier = modifier
  )
}
