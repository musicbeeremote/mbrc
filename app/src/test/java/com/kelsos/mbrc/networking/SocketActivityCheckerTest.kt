package com.kelsos.mbrc.networking

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kelsos.mbrc.utils.TestDispatchers
import com.kelsos.mbrc.utils.testDispatcher
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class SocketActivityCheckerTest {

  private lateinit var checker: SocketActivityChecker
  private lateinit var timeoutListener: SocketActivityChecker.PingTimeoutListener

  @Before
  fun setUp() {
    checker = SocketActivityChecker(TestDispatchers.dispatchers)
    timeoutListener = mockk()
  }

  @After
  fun tearDown() {
  }

  @Test
  fun `no callback on successful ping`() = testDispatcher.runBlockingTest {
    checker.setPingTimeoutListener(timeoutListener)
    checker.start()
    advanceTimeBy(30000)
    checker.ping()
    checker.stop()
    verify(exactly = 0) { timeoutListener.onTimeout() }
  }

  @Test
  fun `callback on elapsed ping`() = testDispatcher.runBlockingTest {
    checker.setPingTimeoutListener(timeoutListener)
    checker.start()
    advanceTimeBy(40000)
    checker.stop()
    verify(exactly = 1) { timeoutListener.onTimeout() }
  }
}
