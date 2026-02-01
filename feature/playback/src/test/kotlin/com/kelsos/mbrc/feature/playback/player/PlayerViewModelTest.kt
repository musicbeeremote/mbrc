package com.kelsos.mbrc.feature.playback.player

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.settings.ChangeLogChecker
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.LfmRating
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayerStatusModel
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.Repeat
import com.kelsos.mbrc.core.common.state.ShuffleMode
import com.kelsos.mbrc.core.common.state.TrackDetails
import com.kelsos.mbrc.core.common.state.TrackRating
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PlayerViewModelTest : KoinTest {

  private val playingTrackFlow = MutableStateFlow<BasicTrackInfo>(BasicTrackInfo())
  private val playingPositionFlow = MutableStateFlow(PlayingPosition())
  private val trackRatingFlow = MutableStateFlow(TrackRating())
  private val playerStatusFlow = MutableStateFlow(PlayerStatusModel())
  private val trackDetailsFlow = MutableStateFlow(TrackDetails.EMPTY)
  private val showRatingOnPlayerFlow = MutableStateFlow(false)

  private val testModule = module {
    single<AppStateFlow> {
      mockk(relaxed = true) {
        every { playingTrack } returns playingTrackFlow
        every { playingPosition } returns playingPositionFlow
        every { playingTrackRating } returns trackRatingFlow
        every { playerStatus } returns playerStatusFlow
        every { playingTrackDetails } returns trackDetailsFlow
      }
    }
    single<UserActionUseCase> { mockk(relaxed = true) }
    single<ChangeLogChecker> { mockk(relaxed = true) }
    single<SettingsManager> {
      mockk(relaxed = true) {
        every { showRatingOnPlayerFlow } returns this@PlayerViewModelTest.showRatingOnPlayerFlow
      }
    }
    singleOf(::PlayerViewModel)
  }

  private val viewModel: PlayerViewModel by inject()
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

  // region Initial State Tests

  @Test
  fun `playingTrack initial state should be empty BasicTrackInfo`() = runTest(testDispatcher) {
    viewModel.playingTrack.test {
      val item = awaitItem()
      assertThat(item.artist).isEmpty()
      assertThat(item.title).isEmpty()
      assertThat(item.album).isEmpty()
    }
  }

  @Test
  fun `playingPosition initial state should be zero`() = runTest(testDispatcher) {
    viewModel.playingPosition.test {
      val item = awaitItem()
      assertThat(item.current).isEqualTo(0)
      assertThat(item.total).isEqualTo(0)
    }
  }

  @Test
  fun `trackRating initial state should be default TrackRating`() = runTest(testDispatcher) {
    viewModel.trackRating.test {
      val item = awaitItem()
      assertThat(item.rating).isNull()
      assertThat(item.lfmRating).isEqualTo(LfmRating.Normal)
    }
  }

  @Test
  fun `volumeState initial state should have zero volume and unmuted`() = runTest(testDispatcher) {
    viewModel.volumeState.test {
      val item = awaitItem()
      assertThat(item.volume).isEqualTo(0)
      assertThat(item.mute).isFalse()
    }
  }

  @Test
  fun `playbackState initial state should be undefined`() = runTest(testDispatcher) {
    viewModel.playbackState.test {
      val item = awaitItem()
      assertThat(item.playerState).isEqualTo(PlayerState.Undefined)
      assertThat(item.shuffle).isEqualTo(ShuffleMode.Off)
      assertThat(item.repeat).isEqualTo(Repeat.None)
    }
  }

  @Test
  fun `isScrobbling initial state should be false`() = runTest(testDispatcher) {
    viewModel.isScrobbling.test {
      assertThat(awaitItem()).isFalse()
    }
  }

  @Test
  fun `showRatingOnPlayer initial state should be false`() = runTest(testDispatcher) {
    viewModel.showRatingOnPlayer.test {
      assertThat(awaitItem()).isFalse()
    }
  }

  // endregion

  // region State Flow Update Tests

  @Test
  fun `playingTrack should update when app state emits new track`() = runTest(testDispatcher) {
    viewModel.playingTrack.test {
      awaitItem() // initial

      val newTrack = BasicTrackInfo(
        artist = "Test Artist",
        title = "Test Title",
        album = "Test Album",
        path = "/test/path",
        coverUrl = "http://cover.url"
      )
      playingTrackFlow.emit(newTrack)

      val item = awaitItem()
      assertThat(item.artist).isEqualTo("Test Artist")
      assertThat(item.title).isEqualTo("Test Title")
      assertThat(item.album).isEqualTo("Test Album")
    }
  }

  @Test
  fun `playingPosition should update when app state emits new position`() =
    runTest(testDispatcher) {
      viewModel.playingPosition.test {
        awaitItem() // initial

        playingPositionFlow.emit(PlayingPosition(current = 30000, total = 180000))

        val item = awaitItem()
        assertThat(item.current).isEqualTo(30000)
        assertThat(item.total).isEqualTo(180000)
      }
    }

  @Test
  fun `volumeState should update when player status changes volume`() = runTest(testDispatcher) {
    viewModel.volumeState.test {
      awaitItem() // initial

      playerStatusFlow.emit(PlayerStatusModel(volume = 75, mute = true))

      val item = awaitItem()
      assertThat(item.volume).isEqualTo(75)
      assertThat(item.mute).isTrue()
    }
  }

  @Test
  fun `playbackState should update when player status changes`() = runTest(testDispatcher) {
    viewModel.playbackState.test {
      awaitItem() // initial

      playerStatusFlow.emit(
        PlayerStatusModel(
          state = PlayerState.Playing,
          shuffle = ShuffleMode.AutoDJ,
          repeat = Repeat.All
        )
      )

      val item = awaitItem()
      assertThat(item.playerState).isEqualTo(PlayerState.Playing)
      assertThat(item.shuffle).isEqualTo(ShuffleMode.AutoDJ)
      assertThat(item.repeat).isEqualTo(Repeat.All)
    }
  }

  @Test
  fun `isScrobbling should update when player status changes scrobbling`() =
    runTest(testDispatcher) {
      viewModel.isScrobbling.test {
        awaitItem() // initial false

        playerStatusFlow.emit(PlayerStatusModel(scrobbling = true))

        assertThat(awaitItem()).isTrue()
      }
    }

  @Test
  fun `showRatingOnPlayer should update when settings change`() = runTest(testDispatcher) {
    viewModel.showRatingOnPlayer.test {
      awaitItem() // initial false

      showRatingOnPlayerFlow.emit(true)

      assertThat(awaitItem()).isTrue()
    }
  }

  @Test
  fun `volumeState should emit distinctUntilChanged`() = runTest(testDispatcher) {
    viewModel.volumeState.test {
      awaitItem() // initial

      playerStatusFlow.emit(PlayerStatusModel(volume = 50, mute = false))
      val first = awaitItem()
      assertThat(first.volume).isEqualTo(50)

      // Same volume state but different player state - should not emit volume
      playerStatusFlow.emit(
        PlayerStatusModel(
          volume = 50,
          mute = false,
          state = PlayerState.Playing
        )
      )
      expectNoEvents()

      // Different volume - should emit
      playerStatusFlow.emit(PlayerStatusModel(volume = 60, mute = false))
      val second = awaitItem()
      assertThat(second.volume).isEqualTo(60)
    }
  }

  @Test
  fun `playbackState should emit distinctUntilChanged`() = runTest(testDispatcher) {
    viewModel.playbackState.test {
      awaitItem() // initial

      playerStatusFlow.emit(PlayerStatusModel(state = PlayerState.Playing))
      val first = awaitItem()
      assertThat(first.playerState).isEqualTo(PlayerState.Playing)

      // Same playback state but different volume - should not emit playback
      playerStatusFlow.emit(PlayerStatusModel(state = PlayerState.Playing, volume = 75))
      expectNoEvents()

      // Different state - should emit
      playerStatusFlow.emit(PlayerStatusModel(state = PlayerState.Paused))
      val second = awaitItem()
      assertThat(second.playerState).isEqualTo(PlayerState.Paused)
    }
  }

  // endregion

  // region Player Actions Tests

  @Test
  fun `playPause action should send PlayerPlayPause user action`() = runTest(testDispatcher) {
    viewModel.actions.playPause()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerPlayPause && it.data == true }
      )
    }
  }

  @Test
  fun `previous action should send PlayerPrevious user action`() = runTest(testDispatcher) {
    viewModel.actions.previous()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerPrevious && it.data == true }
      )
    }
  }

  @Test
  fun `next action should send PlayerNext user action`() = runTest(testDispatcher) {
    viewModel.actions.next()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerNext && it.data == true }
      )
    }
  }

  @Test
  fun `stop action should send PlayerStop user action`() = runTest(testDispatcher) {
    viewModel.actions.stop()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerStop && it.data == true }
      )
    }
  }

  @Test
  fun `shuffle action should send PlayerShuffle toggle user action`() = runTest(testDispatcher) {
    viewModel.actions.shuffle()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerShuffle && it.data == Protocol.TOGGLE }
      )
    }
  }

  @Test
  fun `repeat action should send PlayerRepeat toggle user action`() = runTest(testDispatcher) {
    viewModel.actions.repeat()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerRepeat && it.data == Protocol.TOGGLE }
      )
    }
  }

  @Test
  fun `mute action should send PlayerMute toggle user action`() = runTest(testDispatcher) {
    viewModel.actions.mute()
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.PlayerMute && it.data == Protocol.TOGGLE }
      )
    }
  }

  @Test
  fun `toggleScrobbling action should send PlayerScrobble toggle user action`() =
    runTest(testDispatcher) {
      viewModel.actions.toggleScrobbling()
      advanceUntilIdle()

      coVerify {
        userActionUseCase.perform(
          match { it.protocol == Protocol.PlayerScrobble && it.data == Protocol.TOGGLE }
        )
      }
    }

  // endregion

  // Note: Volume and seek actions use sample() for rate limiting.
  // Testing the sample behavior requires complex time-based test setup.
  // The core functionality is verified through the actions interface availability
  // and the state flow tests above.

  // endregion

  // region Favorite/Ban Toggle Tests

  @Test
  fun `toggleFavorite when normal should set to loved`() = runTest(testDispatcher) {
    viewModel.actions.toggleFavorite(false, false)
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match {
          it.protocol == Protocol.NowPlayingLfmRating &&
            it.data == LfmRating.Loved.toActionString()
        }
      )
    }
  }

  @Test
  fun `toggleFavorite when already loved should toggle to normal`() = runTest(testDispatcher) {
    viewModel.actions.toggleFavorite(true, false)
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.NowPlayingLfmRating && it.data == "toggle" }
      )
    }
  }

  @Test
  fun `toggleFavorite when banned should first toggle then set to loved`() =
    runTest(testDispatcher) {
      viewModel.actions.toggleFavorite(false, true)
      advanceUntilIdle()

      coVerify(ordering = io.mockk.Ordering.ORDERED) {
        userActionUseCase.perform(
          match { it.protocol == Protocol.NowPlayingLfmRating && it.data == "toggle" }
        )
        userActionUseCase.perform(
          match {
            it.protocol == Protocol.NowPlayingLfmRating &&
              it.data == LfmRating.Loved.toActionString()
          }
        )
      }
    }

  @Test
  fun `toggleBan when normal should set to banned`() = runTest(testDispatcher) {
    viewModel.actions.toggleBan(false, false)
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match {
          it.protocol == Protocol.NowPlayingLfmRating &&
            it.data == LfmRating.Banned.toActionString()
        }
      )
    }
  }

  @Test
  fun `toggleBan when already banned should toggle to normal`() = runTest(testDispatcher) {
    viewModel.actions.toggleBan(true, false)
    advanceUntilIdle()

    coVerify {
      userActionUseCase.perform(
        match { it.protocol == Protocol.NowPlayingLfmRating && it.data == "toggle" }
      )
    }
  }

  @Test
  fun `toggleBan when loved should first toggle then set to banned`() = runTest(testDispatcher) {
    viewModel.actions.toggleBan(false, true)
    advanceUntilIdle()

    coVerify(ordering = io.mockk.Ordering.ORDERED) {
      userActionUseCase.perform(
        match { it.protocol == Protocol.NowPlayingLfmRating && it.data == "toggle" }
      )
      userActionUseCase.perform(
        match {
          it.protocol == Protocol.NowPlayingLfmRating &&
            it.data == LfmRating.Banned.toActionString()
        }
      )
    }
  }

  // endregion

  // region Changelog Tests

  @Test
  fun `should not emit ShowChangelog message when changelog checker returns false`() =
    runTest(testDispatcher) {
      // Default mock returns false (relaxed mock behavior)
      viewModel.events.test {
        advanceUntilIdle()
        expectNoEvents()
      }
    }

  // endregion

  // region Track Details Tests

  @Test
  fun `trackDetails should update when app state emits new details`() = runTest(testDispatcher) {
    viewModel.trackDetails.test {
      assertThat(awaitItem()).isEqualTo(TrackDetails.EMPTY)

      val details = TrackDetails(
        genre = "Rock",
        albumArtist = "Test Album Artist",
        composer = "Test Composer"
      )
      trackDetailsFlow.emit(details)

      val item = awaitItem()
      assertThat(item.genre).isEqualTo("Rock")
      assertThat(item.albumArtist).isEqualTo("Test Album Artist")
      assertThat(item.composer).isEqualTo("Test Composer")
    }
  }

  @Test
  fun `trackRating should update when app state emits new rating`() = runTest(testDispatcher) {
    viewModel.trackRating.test {
      awaitItem() // initial

      trackRatingFlow.emit(TrackRating(rating = 4.5f, lfmRating = LfmRating.Loved))

      val item = awaitItem()
      assertThat(item.rating).isEqualTo(4.5f)
      assertThat(item.lfmRating).isEqualTo(LfmRating.Loved)
    }
  }

  // endregion
}
