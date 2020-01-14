package com.kelsos.mbrc.networking.protocol

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.networking.client.MessageQueue
import com.kelsos.mbrc.networking.client.SocketMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VolumeModifyUseCaseImplTest {

    private lateinit var volumeModifyUseCase: VolumeModifyUseCase
    private lateinit var provider: PlayerStatusState
    private lateinit var messageQueue: MessageQueue

    private val slot = slot<SocketMessage>()

    @Before
    fun setUp() {
        provider = mockk()
        messageQueue = mockk()
        volumeModifyUseCase = VolumeModifyUseCaseImpl(provider, messageQueue)
        every { messageQueue.queue(message = capture(slot)) } answers { }
    }

    @Test
    fun `increment volume from 10 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(10) }
        volumeModifyUseCase.increment()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(20)
    }

    @Test
    fun `increment volume from 12 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(12) }
        volumeModifyUseCase.increment()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(20)
    }

    @Test
    fun `increment volume from 17 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(17) }
        volumeModifyUseCase.increment()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(30)
    }

    @Test
    fun `increment volume from 92 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(92) }
        volumeModifyUseCase.increment()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(100)
    }

    @Test
    fun `decrement volume from 7 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(7) }
        volumeModifyUseCase.decrement()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(0)
    }

    @Test
    fun `decrement volume from 10 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(10) }
        volumeModifyUseCase.decrement()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(0)
    }

    @Test
    fun `decrement volume from 17 by a step`() {
        every { provider.getValue() } answers { PlayerStatusModel(17) }
        volumeModifyUseCase.decrement()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(10)
    }

    @Test
    fun `reduce volume from 100`() {
        every { provider.getValue() } answers { PlayerStatusModel(100) }
        volumeModifyUseCase.reduceVolume()
        val message = slot.captured
        assertThat(message.context).isEqualTo(Protocol.PlayerVolume)
        assertThat(message.data).isEqualTo(20)
    }

    @Test
    fun `reduce volume when muted`() {
        every { provider.getValue() } answers { PlayerStatusModel(100, mute = true) }
        volumeModifyUseCase.reduceVolume()
        assertThat(slot.isCaptured).isFalse()
    }

    @Test
    fun `reduce volume when volume is zero`() {
        every { provider.getValue() } answers { PlayerStatusModel(0) }
        volumeModifyUseCase.reduceVolume()
        assertThat(slot.isCaptured).isFalse()
    }
}