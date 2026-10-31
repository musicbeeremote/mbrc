package com.kelsos.mbrc.feature.library.albums

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import org.junit.runner.RunWith
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class ArtistAlbumsViewModelTest :
  FilteredAlbumViewModelContractTest<String, ArtistAlbumsViewModel>() {
  override val viewModelModule = module { singleOf(::ArtistAlbumsViewModel) }
  override val viewModel: ArtistAlbumsViewModel by inject()
  override val key = "Test Artist"

  override fun AlbumRepository.albumsFor(key: String, sort: AlbumSortPreference) =
    getAlbumsByArtist(key, sort.field, sort.order)
}
