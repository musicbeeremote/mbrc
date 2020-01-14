package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusStateImpl
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateMuteTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var update: UpdateMute
  private lateinit var state: PlayerStatusState

  @Before
  fun setUp() {
    state = PlayerStatusStateImpl()
    update = UpdateMute(state)
  }

  @Test
  fun `It should set the mute to false`() {
    state.set(PlayerStatusModel(mute = true))
    update.execute(protocolMessage(status = false))
    assertThat(state.requireValue().mute).isFalse()
  }

  @Test
  fun `It should set the mute to true`() {
    update.execute(protocolMessage(status = true))
    assertThat(state.requireValue().mute).isTrue()
  }

  @Test
  fun `It should set the mute to false if data is a string`() {
    state.set(PlayerStatusModel(mute = true))
    update.execute(protocolMessage(status = false, empty = true))
    assertThat(state.requireValue().mute).isFalse()
  }
}