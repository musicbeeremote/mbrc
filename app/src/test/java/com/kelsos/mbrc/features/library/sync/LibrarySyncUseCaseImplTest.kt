package com.kelsos.mbrc.features.library.sync

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.CoverCache
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.test.KoinTest
import org.koin.test.inject

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class LibrarySyncUseCaseImplTest : KoinTest {

  private val genreRepository: GenreRepository by inject()
  private val artistRepository: ArtistRepository by inject()
  private val albumRepository: AlbumRepository by inject()
  private val trackRepository: TrackRepository by inject()
  private val playlistRepository: PlaylistRepository by inject()
  private val librarySyncUseCase: LibrarySyncUseCase by inject()
  private val syncMetrics: SyncMetrics by inject()
  private val coverCache: CoverCache by inject()

  private val testModule = module {
    singleBy<LibrarySyncUseCase, LibrarySyncUseCaseImpl>()
    single { mockk<GenreRepository>() }
    single { mockk<ArtistRepository>() }
    single { mockk<AlbumRepository>() }
    single { mockk<TrackRepository>() }
    single { mockk<PlaylistRepository>() }
    single { mockk<SyncMetrics>() }
    single { mockk<CoverCache>() }
  }

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
    every { syncMetrics.librarySyncComplete(any()) } answers { }
    every { syncMetrics.librarySyncStarted() } answers { }
    every { syncMetrics.librarySyncFailed() } answers { }
    coEvery { coverCache.cache() } just Runs
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun emptyLibraryAutoSync() = runBlockingTest(testDispatcher) {
    mockCacheState(true)
    mockSuccessfulRepositoryResponse()

    val result = librarySyncUseCase.sync(true, mockk())
    advanceTimeBy(5000)
    assertThat(result).isEqualTo(SyncResult.SUCCESS)
  }

  @Test
  fun nonEmptyLibraryAutoSync() = runBlockingTest(testDispatcher) {
    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    val result = librarySyncUseCase.sync(true, mockk())
    advanceTimeBy(5000)
    assertThat(result).isEqualTo(SyncResult.NOOP)
    assertThat(librarySyncUseCase.isRunning()).isFalse()
  }

  private fun mockCacheState(isEmpty: Boolean) {
    coEvery { genreRepository.cacheIsEmpty() } returns isEmpty
    coEvery { artistRepository.cacheIsEmpty() } returns isEmpty
    coEvery { albumRepository.cacheIsEmpty() } returns isEmpty
    coEvery { trackRepository.cacheIsEmpty() } returns isEmpty
    coEvery { playlistRepository.cacheIsEmpty() } returns isEmpty

    coEvery { genreRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { artistRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { albumRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { trackRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { playlistRepository.count() } returns if (isEmpty) 0 else 1
  }

  private fun mockSuccessfulRepositoryResponse() {
    coEvery { genreRepository.getRemote(any()) } coAnswers {
      delay(400)
      Unit.right()
    }
    coEvery { artistRepository.getRemote(any()) } coAnswers {
      delay(400)
      Unit.right()
    }
    coEvery { albumRepository.getRemote(any()) } coAnswers {
      delay(400)
      Unit.right()
    }
    coEvery { trackRepository.getRemote(any()) } coAnswers {
      delay(400)
      Unit.right()
    }
    coEvery { playlistRepository.getRemote(any()) } coAnswers {
      delay(400)
      Unit.right()
    }
  }
}
