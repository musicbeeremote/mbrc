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
class UpdateLastFmTest {

  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  @get:Rule
  var coroutineTestRule = CoroutineTestRule()

  private lateinit var updateLastFm: UpdateLastFm
  private lateinit var appState: AppState

  @Before
  fun setUp() {
    appState = AppState()
    updateLastFm = UpdateLastFm(appState)
  }

  @Test
  fun `It should change the scrobbling status to false on incoming message`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(createTestProtocolMessage(status = false))
    assertThat(appState.playerStatus.first().scrobbling).isFalse()
  }

  @Test
  fun `It should change the scrobbling status to true on incoming message`() = runTest {
    updateLastFm.execute(createTestProtocolMessage(status = true))
    assertThat(appState.playerStatus.first().scrobbling).isTrue()
  }

  @Test
  fun `It should change the scrobbling status to false for invalid payload`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(createTestProtocolMessage(status = false, empty = true))
    assertThat(appState.playerStatus.first().scrobbling).isFalse()
  }
}
