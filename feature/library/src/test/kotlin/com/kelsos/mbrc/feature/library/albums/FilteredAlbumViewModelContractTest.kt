package com.kelsos.mbrc.feature.library.albums

import android.os.Looper
import androidx.paging.PagingData
import app.cash.turbine.test
import com.kelsos.mbrc.core.common.settings.AlbumSortField
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumRepository
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.robolectric.Shadows.shadowOf

/**
 * The [FilteredAlbumViewModel] contract on top of [AlbumViewModelContractTest]. A subclass names
 * the [key] it loads and the [AlbumRepository] query that [key] maps to in [albumsFor], which the
 * tests call inside MockK `verify` blocks.
 */
abstract class FilteredAlbumViewModelContractTest<K : Any, VM : FilteredAlbumViewModel<K>> :
  AlbumViewModelContractTest<VM>() {
  protected abstract val key: K

  private val nameAscending = AlbumSortPreference(AlbumSortField.NAME, SortOrder.ASC)

  protected abstract fun AlbumRepository.albumsFor(
    key: K,
    sort: AlbumSortPreference
  ): Flow<PagingData<Album>>

  @Before
  fun stubFilteredQueries() {
    every { repository.getAlbumsByArtist(any(), any(), any()) } returns flowOf(PagingData.empty())
    every { repository.getAlbumsByGenre(any(), any(), any()) } returns flowOf(PagingData.empty())
  }

  @Test
  fun `loading a key queries its albums with the saved sort preference`() {
    runTest(testDispatcher) {
      viewModel.albums.test {
        viewModel.load(key)
        testDispatcher.scheduler.advanceUntilIdle()

        awaitItem()
        cancelAndIgnoreRemainingEvents()
      }

      verify(exactly = 1) { repository.albumsFor(key, nameAscending) }
    }
  }

  @Test
  fun `loading the same key again does not query again`() {
    runTest(testDispatcher) {
      viewModel.albums.test {
        viewModel.load(key)
        testDispatcher.scheduler.advanceUntilIdle()
        awaitItem()

        viewModel.load(key)
        settle()
        cancelAndIgnoreRemainingEvents()
      }

      verify(exactly = 1) { repository.albumsFor(key, nameAscending) }
    }
  }

  @Test
  fun `an unchanged sort preference saved again does not query again`() {
    runTest(testDispatcher) {
      val savedSort = MutableSharedFlow<AlbumSortPreference>(replay = 1)
      savedSort.emit(nameAscending)
      every { librarySettings.albumSortPreferenceFlow } returns savedSort

      viewModel.albums.test {
        viewModel.load(key)
        testDispatcher.scheduler.advanceUntilIdle()
        awaitItem()

        savedSort.emit(nameAscending)
        settle()
        cancelAndIgnoreRemainingEvents()
      }

      verify(exactly = 1) { repository.albumsFor(key, nameAscending) }
    }
  }

  @Test
  fun `nothing is queried before a key is loaded`() {
    runTest(testDispatcher) {
      viewModel.albums.test {
        testDispatcher.scheduler.advanceUntilIdle()

        expectNoEvents()
      }

      verify { repository wasNot Called }
    }
  }

  /** Runs both the test dispatcher and the main looper, where `viewModelScope` collects paging. */
  private fun settle() {
    testDispatcher.scheduler.advanceUntilIdle()
    shadowOf(Looper.getMainLooper()).idle()
    testDispatcher.scheduler.advanceUntilIdle()
  }
}
