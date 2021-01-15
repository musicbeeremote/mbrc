package com.kelsos.mbrc.utils

import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.features.minicontrol.MiniControlViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk

fun mockMiniControlViewModel(): MiniControlViewModel {
  val miniViewModel: MiniControlViewModel = mockk()
  every { miniViewModel.playerStatus } answers {
    val status: PlayerStatusState = mockk()
    every { status.observe(any(), any()) } just Runs
    return@answers status
  }
  every { miniViewModel.playingTrack } answers {
    val state: PlayingTrackState = mockk()
    every { state.observe(any(), any()) } just Runs
    return@answers state
  }
  every { miniViewModel.trackPosition } answers {
    val state: TrackPositionState = mockk()
    every { state.observe(any(), any()) } just Runs
    return@answers state
  }
  return miniViewModel
}
