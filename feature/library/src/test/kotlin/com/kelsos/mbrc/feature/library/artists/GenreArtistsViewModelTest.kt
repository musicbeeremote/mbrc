package com.kelsos.mbrc.feature.library.artists

import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.ArtistSortField
import com.kelsos.mbrc.core.common.settings.ArtistSortPreference
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.TrackAction
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.Outcome
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.artist.ArtistRepository
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
class GenreArtistsViewModelTest : KoinTest {
  private val testModule =
    module {
      single<ArtistRepository> { mockk(relaxed = true) }
      single<QueueHandler> { mockk(relaxed = true) }
      single<ConnectionStateFlow> { mockk(relaxed = true) }
      single<LibrarySettings> { mockk(relaxed = true) }
      singleOf(::GenreArtistsViewModel)
    }

  private val viewModel: GenreArtistsViewModel by inject()
  private val repository: ArtistRepository by inject()
  private val queueHandler: QueueHandler by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val librarySettings: LibrarySettings by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    every { repository.getArtistByGenre(any(), any()) } returns flowOf(PagingData.empty())
    every { librarySettings.libraryTrackDefaultActionFlow } returns flowOf(TrackAction.PlayNow)
    every { librarySettings.artistSortPreferenceFlow } returns flowOf(
      ArtistSortPreference(ArtistSortField.NAME, SortOrder.ASC)
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
      val artist =
        Artist(
          id = 1,
          artist = "Test Artist"
        )
      val queueResult = Outcome.Success(15)
      coEvery { queueHandler.queueArtist(Queue.Next, "Test Artist") } returns queueResult

      viewModel.queueResults.test {
        viewModel.queue(Queue.Next, artist)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(Outcome.Success(15))
      }

      coVerify(exactly = 1) { queueHandler.queueArtist(Queue.Next, "Test Artist") }
    }
  }

  @Test
  fun queueShouldEmitOpenArtistAlbumsWhenQueueIsDefault() {
    runTest(testDispatcher) {
      val artist =
        Artist(
          id = 1,
          artist = "Test Artist"
        )

      viewModel.events.test {
        viewModel.queue(Queue.Default, artist)
        testDispatcher.scheduler.advanceUntilIdle()

        val event = awaitItem()
        assertThat(event).isEqualTo(ArtistUiMessage.OpenArtistAlbums(artist))
      }

      coVerify(exactly = 0) { queueHandler.queueArtist(any<Queue>(), any<String>()) }
    }
  }

  @Test
  fun sortPreferenceShouldEmitInitialValue() {
    runTest(testDispatcher) {
      viewModel.sortPreference.test {
        val initial = awaitItem()
        assertThat(initial).isEqualTo(SortOrder.ASC)
        cancelAndIgnoreRemainingEvents()
      }
    }
  }

  @Test
  fun updateSortPreferenceShouldCallLibrarySettings() {
    runTest(testDispatcher) {
      val preference = ArtistSortPreference(ArtistSortField.NAME, SortOrder.DESC)

      viewModel.updateSortPreference(preference)
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify(exactly = 1) { librarySettings.setArtistSortPreference(preference) }
    }
  }
}
