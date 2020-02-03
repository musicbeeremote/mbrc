package com.kelsos.mbrc.features.radio.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.queue.QueueApi
import com.kelsos.mbrc.features.queue.QueueResponse
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Single
import java.net.SocketTimeoutException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RadioViewModelTest {

  private lateinit var repository: RadioRepository
  private lateinit var queueApi: QueueApi
  private lateinit var radioViewModel: RadioViewModel
  private lateinit var observer: (Event<RadioUiMessages>) -> Unit
  private lateinit var slot: CapturingSlot<Event<RadioUiMessages>>

  @Before
  fun setUp() {
    repository = mockk()
    queueApi = mockk()
    observer = mockk()
    slot = slot()
    every { observer(capture(slot)) } just Runs
    every { repository.getAll() } answers { MockFactory(emptyList()) }
    radioViewModel = RadioViewModel(repository, queueApi, TestDispatchers.dispatchers)
  }

  @Test
  fun `should notify the observer that refresh failed`() {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    radioViewModel.emitter.observeOnce(observer)
    radioViewModel.reload()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(RadioUiMessages.RefreshFailed)
  }

  @Test
  fun `should notify the observer that refresh succeeded`() {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    radioViewModel.emitter.observeOnce(observer)
    radioViewModel.reload()
    verify(exactly = 1) { observer(any()) }
    assertThat(slot.captured.peekContent()).isEqualTo(RadioUiMessages.RefreshSuccess)
  }

  @Test
  fun `should call queue and notify success`() {
    val playArguments = slot<List<String>>()
    every { queueApi.queue(Queue.NOW, capture(playArguments)) } answers {
      Single.just(QueueResponse(200))
    }

    radioViewModel.emitter.observeOnce(observer)
    radioViewModel.play("http://radio.station")
    assertThat(playArguments.captured).hasSize(1)
    assertThat(playArguments.captured).containsExactly("http://radio.station")
    assertThat(slot.captured.peekContent()).isEqualTo(RadioUiMessages.QueueSuccess)
  }

  @Test
  fun `should notify on network error`() {
    every { queueApi.queue(Queue.NOW, any()) } throws SocketTimeoutException()
    radioViewModel.emitter.observeOnce(observer)
    radioViewModel.play("http://radio.station")
    assertThat(slot.captured.peekContent()).isEqualTo(RadioUiMessages.NetworkError)
  }

  @Test
  fun `should notify on queue failure`() {
    every { queueApi.queue(Queue.NOW, any()) } answers {
      Single.just(QueueResponse(500))
    }
    radioViewModel.emitter.observeOnce(observer)
    radioViewModel.play("http://radio.station")
    assertThat(slot.captured.peekContent()).isEqualTo(RadioUiMessages.QueueFailed)
  }
}