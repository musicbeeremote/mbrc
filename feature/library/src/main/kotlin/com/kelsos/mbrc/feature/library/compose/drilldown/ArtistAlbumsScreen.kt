package com.kelsos.mbrc.feature.library.compose.drilldown

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.feature.library.R
import com.kelsos.mbrc.feature.library.albums.ArtistAlbumsViewModel
import com.kelsos.mbrc.feature.library.compose.SortOption
import org.koin.androidx.compose.koinViewModel

private val albumSortOptions = listOf(
  SortOption(AlbumSortField.NAME, R.string.sort_by_name)
)

@Composable
fun ArtistAlbumsScreen(
  artistName: String,
  onNavigateBack: () -> Unit,
  onNavigateToAlbumTracks: (Album) -> Unit,
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: ArtistAlbumsViewModel = koinViewModel()
) {
  AlbumDrilldownScreen(
    title = artistName,
    filter = artistName,
    listIdPrefix = "artistalbums",
    sortOptions = albumSortOptions,
    viewModel = viewModel,
    onNavigateBack = onNavigateBack,
    onNavigateToAlbumTracks = onNavigateToAlbumTracks,
    onNavigateToPlayer = onNavigateToPlayer,
    snackbarHostState = snackbarHostState,
    modifier = modifier
  )
}
