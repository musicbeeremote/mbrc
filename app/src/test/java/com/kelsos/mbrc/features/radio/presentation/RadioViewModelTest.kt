package com.kelsos.mbrc.features.radio.presentation

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.TestApplication
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.utils.MockFactory
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.observeOnce
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.net.SocketTimeoutException

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class RadioViewModelTest {

  private lateinit var repository: RadioRepository
  private lateinit var queueApi: QueueApi
  private lateinit var radioViewModel: RadioViewModel

  @Before
  fun setUp() {
    repository = mockk()
    queueApi = mockk()
    every { repository.getAll() } answers { MockFactory(emptyList()) }
    radioViewModel = RadioViewModel(repository, queueApi, TestDispatchers.dispatchers)
  }

  @Test
  fun `should notify the observer that refresh failed`() {
    coEvery { repository.getRemote() } coAnswers { Try.raiseError(SocketTimeoutException()) }
    radioViewModel.reload()
    radioViewModel.emitter.observeOnce { event ->
      assertThat(event.peekContent()).isEqualTo(RadioRefreshResult.RefreshFailed)
    }
  }

  @Test
  fun `should notify the observer that refresh succeeded`() {
    coEvery { repository.getRemote() } coAnswers { Try.invoke { } }
    radioViewModel.reload()
    radioViewModel.emitter.observeOnce { event ->
      assertThat(event.peekContent()).isEqualTo(RadioRefreshResult.RefreshFailed)
    }
  }
}