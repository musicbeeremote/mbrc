package com.kelsos.mbrc.features.radio.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.helper.QueueResult
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class RadioViewModelTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var repository: RadioRepository
  private lateinit var queueHandler: QueueHandler
  private lateinit var radioViewModel: RadioViewModel
  private lateinit var slot: CapturingSlot<Event<RadioUiMessages>>

  @Before
  fun setUp() {
    repository = mockk()
    queueHandler = mockk()
    slot = slot()
    every { repository.getAll() } answers { MockFactory<RadioStation>(emptyList()).flow() }
    radioViewModel = RadioViewModel(repository, queueHandler, appCoroutineDispatchers)
  }

  @Test
  fun `should notify the observer that refresh failed`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(expectItem()).isEqualTo(RadioUiMessages.RefreshFailed)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(expectItem()).isEqualTo(RadioUiMessages.RefreshSuccess)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should call queue and notify success`() = testDispatcher.runBlockingTest {
    val playArguments = slot<String>()
    coEvery { queueHandler.queuePath(capture(playArguments)) } answers {
      QueueResult(true, 0)
    }

    radioViewModel.emitter.test {
      radioViewModel.play("http://radio.station")
      assertThat(expectItem()).isEqualTo(RadioUiMessages.QueueSuccess)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify on network error`() = runBlockingTest(testDispatcher) {
    coEvery { queueHandler.queuePath(any()) } throws SocketTimeoutException()
    radioViewModel.emitter.test {
      radioViewModel.play("http://radio.station")
      assertThat(expectItem()).isEqualTo(RadioUiMessages.NetworkError)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify on queue failure`() = runBlockingTest(testDispatcher) {
    coEvery { queueHandler.queuePath(any()) } answers {
      QueueResult(false, 0)
    }
    radioViewModel.emitter.test {
      radioViewModel.play("http://radio.station")
      assertThat(expectItem()).isEqualTo(RadioUiMessages.QueueFailed)
      cancelAndConsumeRemainingEvents()
    }
  }
}
