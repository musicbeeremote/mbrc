package com.kelsos.mbrc.feature.library.genres

import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.GenreSortField
import com.kelsos.mbrc.core.common.settings.GenreSortPreference
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.SortPreference
import com.kelsos.mbrc.core.common.settings.TrackAction
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.data.library.genre.GenreRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.LibrarySearchModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class BrowseGenreViewModelTest : KoinTest {
  private val searchTermFlow = MutableStateFlow("")

  private val testModule =
    module {
      single<GenreRepository> { mockk(relaxed = true) }
      single<QueueHandler> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<LibrarySettings> { mockk(relaxed = true) }
      single<LibrarySearchModel> { mockk(relaxed = true) }
      singleOf(::BrowseGenreViewModel)
    }

  private val viewModel: BrowseGenreViewModel by inject()
  private val repository: GenreRepository by inject()
  private val queueHandler: QueueHandler by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val librarySettings: LibrarySettings by inject()
  private val searchModel: LibrarySearchModel by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    every { searchModel.term } returns searchTermFlow
    every { repository.getAll() } returns flowOf(PagingData.empty())
    every { repository.getAll(any()) } returns flowOf(PagingData.empty())
    every { repository.search(any()) } returns flowOf(PagingData.empty())
    every { repository.search(any(), any()) } returns flowOf(PagingData.empty())
    every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.PlayNow)
    every { librarySettings.genreSortPreferenceFlow } returns flowOf(
      SortPreference(GenreSortField.NAME, SortOrder.ASC)
    )
    coEvery { connectionStateFlow.isConnected } returns true
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun queueShouldEmitQueueSuccessWhenConnectedAndQueueSucceeds() {
    runTest(testDispatcher) {
      coEvery { connectionStateFlow.isConnected } returns true
      val genre = Genre(id = 1, genre = "Rock")
      val queueResult = Outcome.Success(25)
      coEvery { queueHandler.queueGenre(Queue.Next, "Rock") } returns queueResult

      viewModel.queueResults.test {
        viewModel.queue(Queue.Next, genre)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(Outcome.Success(25))
      }

      coVerify(exactly = 1) { queueHandler.queueGenre(Queue.Next, "Rock") }
    }
  }

  @Test
  fun queueShouldEmitOpenArtistsWhenQueueIsDefault() {
    runTest(testDispatcher) {
      val genre = Genre(id = 1, genre = "Rock")

      viewModel.events.test {
        viewModel.queue(Queue.Default, genre)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(GenreUiMessage.OpenArtists(genre))
      }

      coVerify(exactly = 0) { queueHandler.queueGenre(any(), any()) }
    }
  }

  @Test
  fun sortPreferenceShouldEmitInitialValue() {
    runTest(testDispatcher) {
      viewModel.sortPreference.test {
        val initial = awaitItem()
        assertThat(initial).isEqualTo(
          GenreSortPreference(GenreSortField.NAME, SortOrder.ASC)
        )
        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun updateSortPreferenceShouldCallLibrarySettings() {
    runTest(testDispatcher) {
      val newPreference = GenreSortPreference(GenreSortField.NAME, SortOrder.DESC)
      coEvery { librarySettings.setGenreSortPreference(any()) } returns Unit

      viewModel.updateSortPreference(newPreference)
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify(exactly = 1) { librarySettings.setGenreSortPreference(newPreference) }
    }
  }
}
