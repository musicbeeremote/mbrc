package com.kelsos.mbrc.feature.library.albums

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import com.kelsos.mbrc.feature.library.domain.LibrarySyncUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
    single<LibrarySyncUseCase> { mockk(relaxed = true) }
    single<LibrarySearchModel> { mockk(relaxed = true) }
    singleOf(::BrowseAlbumViewModel)
  }
  override val viewModel: BrowseAlbumViewModel by inject()

  private val librarySyncUseCase: LibrarySyncUseCase by inject()
  private val searchModel: LibrarySearchModel by inject()

  @Before
  fun stubSearchTerm() {
    every { searchModel.term } returns searchTerm
  }

  @Test
  fun `syncing while disconnected reports the network and does not sync`() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns false

      viewModel.events.test {
        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.NetworkUnavailable)
      }

      coVerify(exactly = 0) { librarySyncUseCase.sync() }
    }
  }

  @Test
  fun `syncing while connected runs the sync without reporting anything`() {
    runTest(testDispatcher) {
      viewModel.events.test {
        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()

        expectNoEvents()
      }

      coVerify(exactly = 1) { librarySyncUseCase.sync() }
    }
  }

  @Test
  fun `the connection is checked again on every sync request`() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns true andThen false

      viewModel.events.test {
        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()
        expectNoEvents()

        viewModel.sync()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(awaitItem()).isEqualTo(AlbumUiMessage.NetworkUnavailable)
      }

      coVerify(exactly = 1) { librarySyncUseCase.sync() }
    }
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
