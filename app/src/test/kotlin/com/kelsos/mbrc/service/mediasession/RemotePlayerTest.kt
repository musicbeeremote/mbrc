package com.kelsos.mbrc.service.mediasession

import android.content.Context
import androidx.media3.common.Player
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayerStatusModel
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.ShuffleMode
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.state.TrackRating
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatchers
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.VolumeModifyUseCase
import com.kelsos.mbrc.service.mediasession.RemotePlayer.Companion.POSITION_DRIFT_TOLERANCE_MS
import com.kelsos.mbrc.service.mediasession.RemotePlayer.Companion.departsFrom
import io.mockk.every
import io.mockk.mockk
import java.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.shadows.ShadowSystemClock

/**
 * Covers the state Media3 reads back from the player: what it reports about playback, and which
 * commands it offers.
 *
 * The command handlers (play/pause/seek/volume) are deliberately NOT covered here. They are
 * `protected` on `SimpleBasePlayer`, and driving them through the public `Player` API under
 * Robolectric never reaches them: the command is accepted (`released` is false, the command is in
 * `availableCommands`, the looper matches) but the work the handler launches never runs, because
 * the player's collectors need a `backgroundScope` while its commands need a scheduler that
 * `advanceUntilIdle` actually drives, and the two cannot be satisfied at once here. The throttling
 * behaviour and the #345 non-blocking guarantee therefore remain verified only on a device.
 */
@RunWith(AndroidJUnit4::class)
class RemotePlayerTest {
  private val userActionUseCase: UserActionUseCase = mockk(relaxed = true)
  private val volumeModifyUseCase: VolumeModifyUseCase = mockk(relaxed = true)

  private val status = MutableStateFlow(PlayerStatusModel())
  private val position = MutableStateFlow(PlayingPosition())
  private val track = MutableStateFlow<TrackInfo>(BasicTrackInfo())

  private val appState: AppStateFlow = mockk {
    every { playerStatus } returns status
    every { playingPosition } returns position
    every { playingTrack } returns track
    every { playingTrackRating } returns MutableStateFlow(TrackRating())
    every { lyrics } returns MutableStateFlow(emptyList())
  }

  private fun player(scope: CoroutineScope): RemotePlayer {
    val context: Context = ApplicationProvider.getApplicationContext()
    return RemotePlayer(
      context,
      userActionUseCase,
      volumeModifyUseCase,
      appState,
      testDispatchers,
      scope
    )
  }

  @Test
  fun `a playing state is reported as ready and playing`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Playing, volume = 55, mute = true)
    val player = player(backgroundScope)

    assertThat(player.playWhenReady).isTrue()
    assertThat(player.playbackState).isEqualTo(Player.STATE_READY)
    assertThat(player.deviceVolume).isEqualTo(55)
    assertThat(player.isDeviceMuted).isTrue()
  }

  @Test
  fun `a paused state is still ready but not playing`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Paused)
    val player = player(backgroundScope)

    assertThat(player.playbackState).isEqualTo(Player.STATE_READY)
    assertThat(player.playWhenReady).isFalse()
  }

  @Test
  fun `an undefined state is reported as ended`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Undefined)
    val player = player(backgroundScope)

    assertThat(player.playbackState).isEqualTo(Player.STATE_ENDED)
  }

  @Test
  fun `shuffle is reflected from the player status`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(shuffle = ShuffleMode.Shuffle)
    val player = player(backgroundScope)

    assertThat(player.shuffleModeEnabled).isTrue()
  }

  @Test
  fun `shuffle is off unless the plugin says otherwise`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(shuffle = ShuffleMode.Off)
    val player = player(backgroundScope)

    assertThat(player.shuffleModeEnabled).isFalse()
  }

  /**
   * A stream has no meaningful duration, so offering seek would let the notification scrub to a
   * position the plugin cannot honour.
   */
  @Test
  fun `a stream offers no seek commands`() = runTest(testDispatcher) {
    position.value = PlayingPosition(current = 1_000, total = -1)
    val player = player(backgroundScope)

    val commands = player.availableCommands

    assertThat(commands.contains(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)).isFalse()
    assertThat(commands.contains(Player.COMMAND_SEEK_BACK)).isFalse()
    assertThat(commands.contains(Player.COMMAND_SEEK_FORWARD)).isFalse()
    assertThat(commands.contains(Player.COMMAND_PLAY_PAUSE)).isTrue()
  }

  @Test
  fun `regular content offers seek commands`() = runTest(testDispatcher) {
    position.value = PlayingPosition(current = 1_000, total = 200_000)
    val player = player(backgroundScope)

    val commands = player.availableCommands

    assertThat(commands.contains(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)).isTrue()
    assertThat(commands.contains(Player.COMMAND_SEEK_BACK)).isTrue()
    assertThat(commands.contains(Player.COMMAND_SEEK_FORWARD)).isTrue()
  }

  @Test
  fun `skipping is always available so the notification keeps its buttons`() =
    runTest(testDispatcher) {
      position.value = PlayingPosition(current = 1_000, total = -1)
      val player = player(backgroundScope)

      val commands = player.availableCommands

      assertThat(commands.contains(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)).isTrue()
      assertThat(commands.contains(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)).isTrue()
    }

  @Test
  fun `the position is reported from the app state`() = runTest(testDispatcher) {
    position.value = PlayingPosition(current = 12_345, total = 200_000)
    val player = player(backgroundScope)

    assertThat(player.currentPosition).isEqualTo(12_345)
  }

  /**
   * The ticker no longer refreshes the session (#385), so this extrapolation is what keeps the
   * progress bar moving. `SimpleBasePlayer` builds its state on first access, hence the read
   * before the clock moves.
   */
  @Test
  fun `while playing the session advances the position on its own`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Playing)
    position.value = PlayingPosition(current = 10_000, total = 200_000)
    val player = player(backgroundScope)
    player.contentPosition

    ShadowSystemClock.advanceBy(Duration.ofSeconds(3))

    assertWithMessage("the progress bar must move without the app re-posting every second")
      .that(player.contentPosition)
      .isEqualTo(13_000)
  }

  @Test
  fun `while paused the position holds still`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Paused)
    position.value = PlayingPosition(current = 10_000, total = 200_000)
    val player = player(backgroundScope)
    player.contentPosition

    ShadowSystemClock.advanceBy(Duration.ofSeconds(3))

    assertThat(player.contentPosition).isEqualTo(10_000)
  }

  @Test
  fun `a ticker step the session already extrapolated does not refresh it`() {
    val reported = PlayingPosition(current = 10_000, total = 200_000)

    val tick = reported.copy(current = 11_000)

    assertThat(tick.departsFrom(reported, extrapolatedMs = 11_004, playing = true)).isFalse()
  }

  @Test
  fun `a seek while playing refreshes the session`() {
    val reported = PlayingPosition(current = 10_000, total = 200_000)

    val seek = reported.copy(current = 90_000)

    assertThat(seek.departsFrom(reported, extrapolatedMs = 11_000, playing = true)).isTrue()
  }

  @Test
  fun `drift past the tolerance refreshes the session`() {
    val reported = PlayingPosition(current = 10_000, total = 200_000)
    val extrapolated = 20_000L

    val behind = reported.copy(current = extrapolated - POSITION_DRIFT_TOLERANCE_MS - 1)

    assertThat(behind.departsFrom(reported, extrapolated, playing = true)).isTrue()
  }

  @Test
  fun `any position change while paused refreshes the session`() {
    val reported = PlayingPosition(current = 10_000, total = 200_000)

    val nudge = reported.copy(current = 10_500)

    assertWithMessage("nothing advances the position while paused, so every change is a seek")
      .that(nudge.departsFrom(reported, extrapolatedMs = 10_000, playing = false))
      .isTrue()
  }

  @Test
  fun `the end of a track is not mistaken for drift`() {
    val reported = PlayingPosition(current = 199_000, total = 200_000)

    val atEnd = reported.copy(current = 200_000)

    assertWithMessage("the ticker stops at the duration while Media3 keeps extrapolating past it")
      .that(atEnd.departsFrom(reported, extrapolatedMs = 205_000, playing = true))
      .isFalse()
  }

  @Test
  fun `a stream keeps extrapolating without a duration to stop at`() {
    val reported = PlayingPosition(current = 10_000, total = -1)

    val tick = reported.copy(current = 11_000)

    assertThat(tick.departsFrom(reported, extrapolatedMs = 11_004, playing = true)).isFalse()
  }

  @Test
  fun `a stream refreshed at position zero still reports a duration`() = runTest(testDispatcher) {
    status.value = PlayerStatusModel(state = PlayerState.Playing)
    position.value = PlayingPosition(current = 0, total = -1)
    val player = player(backgroundScope)

    assertWithMessage("a zero duration hides the progress bar for the whole stream")
      .that(player.duration)
      .isGreaterThan(0)
  }

  @Test
  fun `a new duration refreshes the session`() {
    val reported = PlayingPosition(current = 10_000, total = 200_000)

    val longer = PlayingPosition(current = 10_000, total = 300_000)

    assertThat(longer.departsFrom(reported, extrapolatedMs = 10_000, playing = true)).isTrue()
  }
}
