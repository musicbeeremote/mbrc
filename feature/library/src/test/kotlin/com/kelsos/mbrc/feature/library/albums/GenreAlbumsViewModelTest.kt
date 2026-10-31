package com.kelsos.mbrc.feature.library.albums

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import org.junit.runner.RunWith
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class GenreAlbumsViewModelTest :
  FilteredAlbumViewModelContractTest<Long, GenreAlbumsViewModel>() {
  override val viewModelModule = module { singleOf(::GenreAlbumsViewModel) }
  override val viewModel: GenreAlbumsViewModel by inject()
  override val key = 7L

  override fun AlbumRepository.albumsFor(key: Long, sort: AlbumSortPreference) =
    getAlbumsByGenre(key, sort.field, sort.order)
}
