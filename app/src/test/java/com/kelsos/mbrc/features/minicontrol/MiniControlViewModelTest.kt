package com.kelsos.mbrc.features.minicontrol

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.BaseState
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MiniControlViewModelTest {

  val positionState: TrackPositionState =
    object : BaseState<PlayingPosition>(), TrackPositionState {
      override fun setPlaying(playing: Boolean) {
        error("not implemented")
      }
    }
  val trackState: PlayingTrackState = object : BaseState<PlayingTrack>(), PlayingTrackState {}
  val playerStatus: PlayerStatusState =
    object : BaseState<PlayerStatusModel>(), PlayerStatusState {}

  lateinit var miniControlViewModel: MiniControlViewModel
  lateinit var userActionUseCase: UserActionUseCase

  @Before
  fun setUp() {
    userActionUseCase = mockk()
    every { userActionUseCase.perform(any()) } just Runs
    miniControlViewModel = MiniControlViewModel(
      trackState,
      playerStatus,
      positionState,
      userActionUseCase
    )
  }

  @After
  fun tearDown() {
  }

  @Test
  fun `pressing next should send a play next message`() {
    miniControlViewModel.next()
    val capturingSlot = slot<UserAction>()
    verify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerNext)
  }

  @Test
  fun `pressing previous should send a play previous message`() {
    miniControlViewModel.previous()
    val capturingSlot = slot<UserAction>()
    verify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerPrevious)
  }

  @Test
  fun `pressing playPause should send a playPause message`() {
    miniControlViewModel.playPause()
    val capturingSlot = slot<UserAction>()
    verify(exactly = 1) { userActionUseCase.perform(capture(capturingSlot)) }
    assertThat(capturingSlot.captured.protocol).isEqualTo(Protocol.PlayerPlayPause)
  }
}
