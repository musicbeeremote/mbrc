package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.rules.CoroutineTestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateMuteTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private lateinit var update: UpdateMute
  private lateinit var appState: AppState

  @Before
  fun setUp() {
    appState = AppState()
    update = UpdateMute(appState)
  }

  @Test
  fun `It should set the mute to false`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(mute = true))
    update.execute(createTestProtocolMessage(status = false))
    assertThat(appState.playerStatus.first().mute).isFalse()
  }

  @Test
  fun `It should set the mute to true`() = runTest {
    update.execute(createTestProtocolMessage(status = true))
    assertThat(appState.playerStatus.first().mute).isTrue()
  }

  @Test
  fun `It should set the mute to false if data is a string`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(mute = true))
    update.execute(createTestProtocolMessage(status = false, empty = true))
    assertThat(appState.playerStatus.first().mute).isFalse()
  }
}
