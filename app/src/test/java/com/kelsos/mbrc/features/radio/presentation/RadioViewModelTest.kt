package com.kelsos.mbrc.features.radio.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.features.queue.QueueResult
import com.kelsos.mbrc.features.queue.QueueUseCase
import com.kelsos.mbrc.features.radio.RadioRepository
import com.kelsos.mbrc.features.radio.RadioStation
import com.kelsos.mbrc.features.radio.RadioUiMessages
import com.kelsos.mbrc.features.radio.RadioViewModel
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
  private lateinit var radioViewModel: RadioViewModel
  private lateinit var queue: QueueUseCase
  private lateinit var slot: CapturingSlot<Event<RadioUiMessages>>

  @Before
  fun setUp() {
    repository = mockk()
    queue = mockk()
    slot = slot()
    every { repository.getAll() } answers { MockFactory<RadioStation>(emptyList()).flow() }
    radioViewModel = RadioViewModel(repository, queue, appCoroutineDispatchers)
  }

  @Test
  fun `should notify the observer that refresh failed`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote(any()) } coAnswers { SocketTimeoutException().left() }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(awaitItem()).isEqualTo(RadioUiMessages.RefreshFailed)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote(any()) } coAnswers { Unit.right() }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(awaitItem()).isEqualTo(RadioUiMessages.RefreshSuccess)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should call queue and notify success`() = testDispatcher.runBlockingTest {
    val playArguments = slot<String>()
    coEvery { queue.queuePath(capture(playArguments)) } answers {
      QueueResult(true, 0)
    }

    radioViewModel.emitter.test {
      radioViewModel.play("http://radio.station")
      assertThat(awaitItem()).isEqualTo(RadioUiMessages.QueueSuccess)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify on queue failure`() = runBlockingTest(testDispatcher) {
    coEvery { queue.queuePath(any()) } answers {
      QueueResult(false, 0)
    }
    radioViewModel.emitter.test {
      radioViewModel.play("http://radio.station")
      assertThat(awaitItem()).isEqualTo(RadioUiMessages.QueueFailed)
      cancelAndConsumeRemainingEvents()
    }
  }
}
