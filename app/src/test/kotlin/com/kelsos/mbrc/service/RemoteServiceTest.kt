package com.kelsos.mbrc.service

import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.networking.ClientConnectionManager
import com.kelsos.mbrc.service.mediasession.AppNotificationManager
import com.kelsos.mbrc.state.AppStateManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class RemoteServiceTest : KoinTest {
  private val receiver: NotificationActionReceiver = mockk(relaxed = true)
  private val appStateManager: AppStateManager = mockk(relaxed = true)
  private val notificationManager: AppNotificationManager = mockk(relaxed = true)
  private val connectionManager: ClientConnectionManager = mockk(relaxed = true)

  private val testModule = module {
    single { receiver }
    single { appStateManager }
    single { notificationManager }
    single { connectionManager }
  }

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Application>()
    val channelId = "test-channel"
    val placeholder = NotificationCompat.Builder(context, channelId)
      .setSmallIcon(android.R.drawable.ic_media_play)
      .build()
    every { notificationManager.createPlaceholder() } returns placeholder
    every { receiver.filter(any()) } returns IntentFilter()

    startKoin { modules(testModule) }
    ServiceState.setRunning(false)
    ServiceState.setStopping(false)
  }

  @After
  fun tearDown() {
    ServiceState.setRunning(false)
    ServiceState.setStopping(false)
    stopKoin()
  }

  @Test
  fun `onStartCommand returns START_NOT_STICKY so the framework will not restart in background`() {
    val service = Robolectric.buildService(RemoteService::class.java).create().get()

    val result = service.onStartCommand(Intent(), 0, 1)

    assertThat(result).isEqualTo(Service.START_NOT_STICKY)
  }

  @Test
  fun `onCreate promotes the service to the foreground and marks it running`() {
    val service = Robolectric.buildService(RemoteService::class.java).create().get()

    assertThat(shadowOf(service).lastForegroundNotification).isNotNull()
    assertThat(ServiceState.isRunning).isTrue()
  }

  @Test
  fun `onStartCommand starts connection and app state`() {
    val service = Robolectric.buildService(RemoteService::class.java).create().get()

    service.onStartCommand(Intent(), 0, 1)

    verify { appStateManager.start() }
    verify { connectionManager.start() }
  }
}
