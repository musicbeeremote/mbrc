package com.kelsos.mbrc.networking.protocol

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.PlayerStatusModel
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VolumeModifyUseCaseImplTest {

  private lateinit var volumeModifyUseCase: VolumeModifyUseCase
  private lateinit var appState: AppState
  private lateinit var messageQueue: MessageQueue

  private val slot = slot<SocketMessage>()

  @Before
  fun setUp() {
    appState = AppState()
    messageQueue = mockk()
    volumeModifyUseCase = VolumeModifyUseCaseImpl(appState, messageQueue)
    coEvery { messageQueue.queue(message = capture(slot)) } just Runs
  }

  @Test
  fun `increment volume from 10 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(10))
    volumeModifyUseCase.increment()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(20)
  }

  @Test
  fun `increment volume from 12 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(12))
    volumeModifyUseCase.increment()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(20)
  }

  @Test
  fun `increment volume from 17 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(17))
    volumeModifyUseCase.increment()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(30)
  }

  @Test
  fun `increment volume from 92 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(92))
    volumeModifyUseCase.increment()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(100)
  }

  @Test
  fun `decrement volume from 7 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(7))
    volumeModifyUseCase.decrement()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(0)
  }

  @Test
  fun `decrement volume from 10 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(10))
    volumeModifyUseCase.decrement()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(0)
  }

  @Test
  fun `decrement volume from 17 by a step`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(17))
    volumeModifyUseCase.decrement()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(10)
  }

  @Test
  fun `reduce volume from 100`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(100))
    volumeModifyUseCase.reduceVolume()
    val message = slot.captured
    assertThat(message.context).isEqualTo(Protocol.PLAYER_VOLUME)
    assertThat(message.data).isEqualTo(20)
  }

  @Test
  fun `reduce volume when muted`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(100, mute = true))
    volumeModifyUseCase.reduceVolume()
    assertThat(slot.isCaptured).isFalse()
  }

  @Test
  fun `reduce volume when volume is zero`() = runTest {
    appState.playerStatus.emit(PlayerStatusModel(0))
    volumeModifyUseCase.reduceVolume()
    assertThat(slot.isCaptured).isFalse()
  }
}
