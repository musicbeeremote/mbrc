package com.kelsos.mbrc.service

import android.app.Application
import android.app.ForegroundServiceStartNotAllowedException
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class ServiceCheckerImplTest {
  private val application: Application = mockk(relaxed = true)
  private val serviceChecker = ServiceCheckerImpl(application)

  @Before
  fun setUp() {
    ServiceState.setRunning(false)
  }

  @After
  fun tearDown() {
    ServiceState.setRunning(false)
  }

  @Test
  fun `a start refused by the system does not crash the caller`() {
    // Android 12+ refuses a foreground service start from the background. #322 guarded the
    // service side; this is the request side, which the crash came back through (#344).
    every { application.startForegroundService(any()) } throws
      ForegroundServiceStartNotAllowedException("not allowed")

    serviceChecker.startServiceIfNotRunning()

    verify { application.startForegroundService(any<Intent>()) }
  }

  @Test
  fun `the service is requested when it is not already running`() {
    serviceChecker.startServiceIfNotRunning()

    verify(exactly = 1) { application.startForegroundService(any<Intent>()) }
  }

  @Test
  fun `nothing is requested while the service is already running`() {
    ServiceState.setRunning(true)

    serviceChecker.startServiceIfNotRunning()

    verify(exactly = 0) { application.startForegroundService(any<Intent>()) }
  }
}
