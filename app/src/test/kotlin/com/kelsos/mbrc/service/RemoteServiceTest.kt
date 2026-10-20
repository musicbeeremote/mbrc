package com.kelsos.mbrc.service

import android.app.Application
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayerStatusModel
import com.kelsos.mbrc.core.networking.ClientConnectionManager
import com.kelsos.mbrc.service.mediasession.AppNotificationManager
import com.kelsos.mbrc.state.AppStateManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
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

  /**
   * The service no longer reads these. They stay in the graph so that a keep-alive check brought
   * back into [RemoteService.onTaskRemoved] would see a connected, playing remote and fail the
   * task-removal test instead of passing by default.
   */
  private val connectionState: ConnectionStateFlow = mockk(relaxed = true)
  private val appState: AppStateFlow = mockk(relaxed = true)
  private val playerStatus = MutableStateFlow(PlayerStatusModel())

  private val testModule = module {
    single { receiver }
    single { appStateManager }
    single { notificationManager }
    single { connectionManager }
    single { connectionState }
    single { appState }
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
    every { appState.playerStatus } returns playerStatus

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

  @Test
  fun `dismissing the app while music is playing still stops the service`() {
    every { connectionState.isConnected } returns true
    playerStatus.value = PlayerStatusModel(state = PlayerState.Playing)
    val service = Robolectric.buildService(RemoteService::class.java).create().get()

    service.onTaskRemoved(Intent())

    assertWithMessage("swiping the app away is the only way to quit the remote while it plays")
      .that(shadowOf(service).isStoppedBySelf)
      .isTrue()
  }

  @Test
  fun `destroying the service releases the session, the state ticker and the connection`() {
    val controller = Robolectric.buildService(RemoteService::class.java).create()

    controller.destroy()

    verify { notificationManager.destroy() }
    verify { appStateManager.stop() }
    verify { connectionManager.stop() }
    assertWithMessage("the playing notification must go with the service")
      .that(shadowOf(controller.get()).isForegroundStopped)
      .isTrue()
  }
}
