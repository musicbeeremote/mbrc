package com.kelsos.mbrc.feature.playback.lyrics

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayerStatusModel
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
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
class LyricsViewModelTest : KoinTest {
  private val lyricsFlow = MutableStateFlow<List<String>>(emptyList())
  private val playingTrackFlow = MutableStateFlow<TrackInfo>(BasicTrackInfo())
  private val playingPositionFlow = MutableStateFlow(PlayingPosition())
  private val playerStatusFlow = MutableStateFlow(PlayerStatusModel())

  private val testModule =
    module {
      single<AppStateFlow> {
        mockk(relaxed = true) {
          every { lyrics } returns lyricsFlow
          every { playingTrack } returns playingTrackFlow
          every { playingPosition } returns playingPositionFlow
          every { playerStatus } returns playerStatusFlow
        }
      }
      single<UserActionUseCase> { mockk(relaxed = true) }
      singleOf(::LyricsViewModel)
    }

  private val viewModel: LyricsViewModel by inject()
  private val userActionUseCase: UserActionUseCase by inject()

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun lyricsFlowShouldEmitLyricsFromAppState() {
    runTest(testDispatcher) {
      // Given
      val testLyrics = listOf("Line 1", "Line 2", "Line 3")

      // When & Then
      viewModel.lyrics.test {
        assertThat(awaitItem()).isEmpty()

        lyricsFlow.emit(testLyrics)
        assertThat(awaitItem()).isEqualTo(testLyrics)
      }
    }
  }

  @Test fun playingTrackShouldReflectAppState() {
    runTest(testDispatcher) {
      // Given
      val testTrack = BasicTrackInfo(
        title = "Test Song",
        artist = "Test Artist",
        album = "Test Album"
      )

      // When
      playingTrackFlow.emit(testTrack)
      testDispatcher.scheduler.advanceUntilIdle()

      // Then
      assertThat(viewModel.playingTrack.value).isEqualTo(testTrack)
    }
  }

  @Test
  fun playingPositionShouldReflectAppState() {
    runTest(testDispatcher) {
      // Given
      val testPosition = PlayingPosition(current = 60000, total = 180000)

      // When
      playingPositionFlow.emit(testPosition)
      testDispatcher.scheduler.advanceUntilIdle()

      // Then
      assertThat(viewModel.playingPosition.value).isEqualTo(testPosition)
    }
  }

  @Test
  fun isPlayingShouldBeTrueWhenPlayerStateIsPlaying() {
    runTest(testDispatcher) {
      // Given
      val playingStatus = PlayerStatusModel(state = PlayerState.Playing)

      // When & Then
      viewModel.isPlaying.test {
        assertThat(awaitItem()).isFalse()

        playerStatusFlow.emit(playingStatus)
        assertThat(awaitItem()).isTrue()
      }
    }
  }

  @Test
  fun isPlayingShouldBeFalseWhenPlayerStateIsPaused() {
    runTest(testDispatcher) {
      // Given
      val pausedStatus = PlayerStatusModel(state = PlayerState.Paused)

      // When & Then
      viewModel.isPlaying.test {
        assertThat(awaitItem()).isFalse()

        playerStatusFlow.emit(pausedStatus)
        // Should still be false since Paused != Playing
        expectNoEvents()
      }
    }
  }

  @Test
  fun isPlayingShouldBeFalseWhenPlayerStateIsStopped() {
    runTest(testDispatcher) {
      // Given
      val stoppedStatus = PlayerStatusModel(state = PlayerState.Stopped)

      // When & Then
      viewModel.isPlaying.test {
        assertThat(awaitItem()).isFalse()

        playerStatusFlow.emit(stoppedStatus)
        // Should still be false since Stopped != Playing
        expectNoEvents()
      }
    }
  }

  @Test
  fun playPauseShouldCallUserActionUseCase() {
    runTest(testDispatcher) {
      // When
      viewModel.playPause()
      testDispatcher.scheduler.advanceUntilIdle()

      // Then - extension function calls perform with UserAction
      coVerify(exactly = 1) {
        userActionUseCase.perform(match { it.protocol == Protocol.PlayerPlayPause })
      }
    }
  }

  @Test
  fun seekShouldCallUserActionUseCaseWithPosition() {
    runTest(testDispatcher) {
      // Given
      val seekPosition = 90000

      // When
      viewModel.seek(seekPosition)
      testDispatcher.scheduler.advanceUntilIdle()

      // Then - extension function calls perform with UserAction containing protocol and data
      coVerify(exactly = 1) {
        userActionUseCase.perform(
          match { it.protocol == Protocol.NowPlayingPosition && it.data == seekPosition }
        )
      }
    }
  }

  @Test
  fun multipleSeekCallsShouldCallUserActionMultipleTimes() {
    runTest(testDispatcher) {
      // When
      viewModel.seek(30000)
      viewModel.seek(60000)
      viewModel.seek(90000)
      testDispatcher.scheduler.advanceUntilIdle()

      // Then
      coVerify(exactly = 3) {
        userActionUseCase.perform(match { it.protocol == Protocol.NowPlayingPosition })
      }
    }
  }

  @Test
  fun isPlayingTransitionFromPlayingToPaused() {
    runTest(testDispatcher) {
      // When & Then
      viewModel.isPlaying.test {
        assertThat(awaitItem()).isFalse()

        playerStatusFlow.emit(PlayerStatusModel(state = PlayerState.Playing))
        assertThat(awaitItem()).isTrue()

        playerStatusFlow.emit(PlayerStatusModel(state = PlayerState.Paused))
        assertThat(awaitItem()).isFalse()
      }
    }
  }
}
