package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.utils.testDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdateLastFmTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var updateLastFm: UpdateLastFm
  private lateinit var appState: AppState

  @Before
  fun setUp() {
    appState = AppState()
    updateLastFm = UpdateLastFm(appState)
  }

  @Test
  fun `It should change the scrobbling status to false on incoming message`() = runBlockingTest(
    testDispatcher
  ) {
    appState.playerStatus.emit(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(protocolMessage(status = false))
    assertThat(appState.playerStatus.first().scrobbling).isFalse()
  }

  @Test
  fun `It should change the scrobbling status to true on incoming message`() = runBlockingTest(
    testDispatcher
  ) {
    updateLastFm.execute(protocolMessage(status = true))
    assertThat(appState.playerStatus.first().scrobbling).isTrue()
  }

  @Test
  fun `It should change the scrobbling status to false for invalid payload`() = runBlockingTest(
    testDispatcher
  ) {
    appState.playerStatus.emit(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(protocolMessage(status = false, empty = true))
    assertThat(appState.playerStatus.first().scrobbling).isFalse()
  }
}
