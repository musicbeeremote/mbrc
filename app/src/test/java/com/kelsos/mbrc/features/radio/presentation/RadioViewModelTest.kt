package com.kelsos.mbrc.features.radio.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.appCoroutineDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
class RadioViewModelTest {

  private lateinit var repository: RadioRepository
  private lateinit var queueApi: QueueHandler
  private lateinit var radioViewModel: RadioViewModel

  @Before
  fun setUp() {
    repository = mockk()
    queueApi = mockk()
    every { repository.getAll() } answers { flow { MockFactory(emptyList()) } }
    radioViewModel = RadioViewModel(repository, queueApi, appCoroutineDispatchers)
  }

  @Test
  fun `should notify the observer that refresh failed`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(expectItem()).isEqualTo(RadioRefreshResult.RefreshFailed)
      cancelAndConsumeRemainingEvents()
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() = runBlockingTest(testDispatcher) {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    radioViewModel.emitter.test {
      radioViewModel.reload()
      assertThat(expectItem()).isEqualTo(RadioRefreshResult.RefreshSuccess)
      cancelAndConsumeRemainingEvents()
    }
  }
}
