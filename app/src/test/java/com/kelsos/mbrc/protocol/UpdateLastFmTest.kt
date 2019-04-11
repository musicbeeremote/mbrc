package com.kelsos.mbrc.protocol

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusStateImpl
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class UpdateLastFmTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var updateLastFm: UpdateLastFm
  private lateinit var statusState: PlayerStatusState

  @Before
  fun setUp() {
    statusState = PlayerStatusStateImpl()
    updateLastFm = UpdateLastFm(statusState)
  }

  @Test
  fun `It should change the scrobbling status to false on incoming message`() {
    statusState.set(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(protocolMessage(status = false))
    assertThat(statusState.requireValue().scrobbling).isFalse()
  }

  @Test
  fun `It should change the scrobbling status to true on incoming message`() {
    updateLastFm.execute(protocolMessage(status = true))
    assertThat(statusState.requireValue().scrobbling).isTrue()
  }

  @Test
  fun `It should change the scrobbling status to false if the payload is not boolean`() {
    statusState.set(PlayerStatusModel(scrobbling = true))
    updateLastFm.execute(protocolMessage(status = false, empty = true))
    assertThat(statusState.requireValue().scrobbling).isFalse()
  }
}