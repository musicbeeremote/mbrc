package com.kelsos.mbrc.feature.library.albums

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class BrowseAlbumViewModelTest : AlbumViewModelContractTest<BrowseAlbumViewModel>() {
  private val searchTerm = MutableStateFlow("")

  override val viewModelModule = module {
    single<LibrarySearchModel> { mockk(relaxed = true) }
    singleOf(::BrowseAlbumViewModel)
  }
  override val viewModel: BrowseAlbumViewModel by inject()

  private val searchModel: LibrarySearchModel by inject()

  @Before
  fun stubSearchTerm() {
    every { searchModel.term } returns searchTerm
  }

  @Test
  fun `an empty search term lists every album`() {
    runTest(testDispatcher) {
      viewModel.albums.test {
        testDispatcher.scheduler.advanceUntilIdle()

        awaitItem()
        cancelAndIgnoreRemainingEvents()
      }

      verify(exactly = 1) { repository.getAll(AlbumSortField.NAME, SortOrder.ASC) }
      verify(exactly = 0) { repository.search(any(), any(), any()) }
    }
  }

  @Test
  fun `a search term searches the albums`() {
    runTest(testDispatcher) {
      searchTerm.value = "abbey"

      viewModel.albums.test {
        testDispatcher.scheduler.advanceUntilIdle()

        awaitItem()
        cancelAndIgnoreRemainingEvents()
      }

      verify(exactly = 1) { repository.search("abbey", AlbumSortField.NAME, SortOrder.ASC) }
    }
  }
}
