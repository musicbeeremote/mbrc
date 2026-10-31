package com.kelsos.mbrc.feature.settings.domain

import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatchers
import com.kelsos.mbrc.core.networking.ClientConnectionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultConnectionSwitcherTest {

  private val repository = mockk<ConnectionRepository>(relaxed = true)
  private val clientConnectionUseCase = mockk<ClientConnectionUseCase>(relaxed = true)
  private val switcher =
    DefaultConnectionSwitcherImpl(repository, clientConnectionUseCase, testDispatchers)

  private val stored = ConnectionSettings(
    address = "192.168.1.5",
    port = 3000,
    name = "Den PC",
    isDefault = true,
    id = 7
  )

  @Test
  fun `switching to a saved host makes it the default and reconnects`() = runTest(testDispatcher) {
    switcher.switchTo(stored)

    coVerifyOrder {
      repository.setDefault(stored)
      clientConnectionUseCase.connect(reset = true, cycleInfo = null)
    }
  }

  @Test
  fun `switching to an address reports the stored host before reconnecting`() =
    runTest(testDispatcher) {
      coEvery { repository.saveAsDefault("192.168.1.5", 3000, "LIVING-ROOM-PC") } returns stored
      val reported = mutableListOf<ConnectionSettings>()
      var connectedBeforeReport = false
      coEvery { clientConnectionUseCase.connect(any(), any()) } answers {
        connectedBeforeReport = reported.isEmpty()
        true
      }

      switcher.switchTo("192.168.1.5", 3000, "LIVING-ROOM-PC") { reported += it }

      assertWithMessage("the stored row wins, so a user's own name for the host is what is shown")
        .that(reported)
        .containsExactly(stored)
      assertWithMessage("the announcement must not wait for the connection attempt")
        .that(connectedBeforeReport)
        .isFalse()
    }

  @Test
  fun `a switch cancelled while saving still reconnects`() = runTest(testDispatcher) {
    coEvery { repository.saveAsDefault(any(), any(), any()) } coAnswers {
      delay(SAVE_MS)
      stored
    }

    val job = launch { switcher.switchTo("192.168.1.5", 3000, "Den PC") {} }
    runCurrent()
    job.cancel()
    advanceUntilIdle()

    assertWithMessage("the caller did cancel, so the reconnect below ran despite it")
      .that(job.isCancelled)
      .isTrue()
    coVerify { clientConnectionUseCase.connect(reset = true, cycleInfo = null) }
  }

  private companion object {
    const val SAVE_MS = 1_000L
  }
}
