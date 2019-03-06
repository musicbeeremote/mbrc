package com.kelsos.mbrc.ui.navigation.library

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.LibrarySyncUseCaseImpl
import io.mockk.coEvery
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.experimental.builder.create
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.StandAloneContext.stopKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.koin.test.declareMock
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LibrarySyncUseCaseImplTest : KoinTest {

  private val genreRepository: GenreRepository by inject()
  private val artistRepository: ArtistRepository by inject()
  private val albumRepository: AlbumRepository by inject()
  private val trackRepository: TrackRepository by inject()
  private val playlistRepository: PlaylistRepository by inject()

  private val librarySyncUseCase: LibrarySyncUseCase by inject()

  private val testModule = module {
    single<LibrarySyncUseCase> { create<LibrarySyncUseCaseImpl>() }
  }

  @Before
  fun setUp() {
    startKoin(listOf(testModule))
    declareMock<GenreRepository>()
    declareMock<ArtistRepository>()
    declareMock<AlbumRepository>()
    declareMock<TrackRepository>()
    declareMock<PlaylistRepository>()
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun emptyLibraryAutoSync() {
    mockCacheState(true)
    mockSuccessfulRepositoryResponse()

    runBlocking {

      val result = librarySyncUseCase.sync(true)
      assertThat(result.isRight()).isTrue()
      assertThat(result.toOption().nonEmpty()).isTrue()
    }
  }

  @Test
  fun nonEmptyLibraryAutoSync() {

    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    runBlocking {

      val result = librarySyncUseCase.sync(true)
      assertThat(result.isRight()).isTrue()
      assertThat(result.toOption().nonEmpty()).isFalse()
    }

    assertThat(librarySyncUseCase.isRunning()).isFalse()
  }

  @Test
  fun nonEmptyLibraryManualSyncTwiceConsecutiveCalled() {
  }

  @Test
  fun nonEmptyLibraryManualSyncAndSecondAfterCompletion() {
  }

  @Test
  fun nonEmptyLibraryManualSyncFailure() {
  }

  private fun mockCacheState(isEmpty: Boolean) {
    coEvery { genreRepository.cacheIsEmpty() } returns isEmpty
    coEvery { artistRepository.cacheIsEmpty() } returns isEmpty
    coEvery { albumRepository.cacheIsEmpty() } returns isEmpty
    coEvery { trackRepository.cacheIsEmpty() } returns isEmpty
  }

  private fun mockSuccessfulRepositoryResponse() {
    coEvery { genreRepository.getRemote() } coAnswers { delay(1) }
    coEvery { artistRepository.getRemote() } coAnswers { delay(1) }
    coEvery { albumRepository.getRemote() } coAnswers { delay(1) }
    coEvery { trackRepository.getRemote() } coAnswers { delay(1) }
    coEvery { playlistRepository.getRemote() } coAnswers { delay(1) }
  }

  private fun mockFailedRepositoryResponse() {
    coEvery { genreRepository.getRemote() } coAnswers { delay(1) }
    coEvery { artistRepository.getRemote() } throws IOException("mockio")
    coEvery { albumRepository.getRemote() } coAnswers { delay(1) }
    coEvery { trackRepository.getRemote() } coAnswers { delay(1) }
    coEvery { playlistRepository.getRemote() } coAnswers { delay(1) }
  }
}