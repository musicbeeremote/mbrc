package com.kelsos.mbrc.service.mediasession

import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaSession
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.R
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayerStatusModel
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatchers
import com.kelsos.mbrc.core.platform.intents.MediaIntentBuilder
import com.kelsos.mbrc.core.platform.mediasession.NotificationData
import com.kelsos.mbrc.core.platform.state.PlayingTrack
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationBuilderTest {
  private lateinit var app: Application
  private lateinit var builder: NotificationBuilder

  private val intentBuilder: MediaIntentBuilder = mockk {
    every { getPendingIntent(any(), any()) } answers {
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        0,
        Intent("test"),
        PendingIntent.FLAG_IMMUTABLE
      )
    }
  }

  private val track = PlayingTrack(
    artist = "Tool",
    title = "Lateralus",
    album = "Lateralus"
  )

  /**
   * [testDispatcher] is a single shared instance, and Robolectric keeps its sandbox alive across
   * test classes, so a scope left running here would outlive this class. [RemotePlayer] collects
   * two `sample`-based flows that never complete, and leaving those queued on the shared scheduler
   * stops it ever going idle again, hanging the next test that waits for it.
   */
  private val playerScope = CoroutineScope(testDispatcher + Job())

  @Before
  fun setUp() {
    app = ApplicationProvider.getApplicationContext()
    builder = NotificationBuilder(app, intentBuilder)
  }

  @After
  fun tearDown() {
    playerScope.cancel()
  }

  private fun Notification.title(): String? = extras.getString(Notification.EXTRA_TITLE)

  private fun Notification.text(): String? = extras.getString(Notification.EXTRA_TEXT)

  private fun Notification.subText(): String? = extras.getString(Notification.EXTRA_SUB_TEXT)

  @Test
  fun `the placeholder notification says the app is starting`() {
    val notification = builder.createPlaceholderBuilder().build()

    val name = app.getString(R.string.application_name)
    val starting = app.getString(R.string.application_starting)
    assertThat(notification.title()).isEqualTo(name)
    assertThat(notification.text()).isEqualTo(starting)
  }

  @Test
  fun `the placeholder notification offers a way to cancel`() {
    val notification = builder.createPlaceholderBuilder().build()

    assertThat(NotificationCompat.getActionCount(notification)).isEqualTo(1)
    val cancel = checkNotNull(NotificationCompat.getAction(notification, 0))
    assertThat(cancel.title.toString()).isEqualTo(app.getString(android.R.string.cancel))
  }

  /**
   * A real [androidx.media3.session.MediaSession] is used because MockK cannot mock it under
   * Robolectric: retransforming the final class fails with "attempted to change the class
   * modifiers".
   */
  private fun notificationFor(data: NotificationData): Notification {
    val appState: AppStateFlow = mockk {
      every { playerStatus } returns MutableStateFlow(PlayerStatusModel())
      every { playingPosition } returns MutableStateFlow(PlayingPosition())
      every { playingTrack } returns MutableStateFlow<TrackInfo>(BasicTrackInfo())
    }
    val player = RemotePlayer(
      app,
      mockk(relaxed = true),
      mockk(relaxed = true),
      appState,
      testDispatchers,
      playerScope
    )
    val session = MediaSession.Builder(app, player).build()
    return try {
      builder.createBuilder(data, session).build()
    } finally {
      session.release()
    }
  }

  @Test
  fun `the notification shows the track, artist and album`() {
    val notification = notificationFor(NotificationData(track = track))

    assertThat(notification.title()).isEqualTo("Lateralus")
    assertThat(notification.text()).isEqualTo("Tool")
    assertThat(notification.subText()).isEqualTo("Lateralus")
  }

  @Test
  fun `a stream is labelled live instead of showing the album or a running clock`() {
    val notification = notificationFor(NotificationData(track = track, isStream = true))

    assertThat(notification.subText()).isEqualTo(app.getString(R.string.notification__live))
  }

  @Test
  fun `the notification carries previous, play-pause and next`() {
    val notification = notificationFor(NotificationData(track = track))

    assertThat(NotificationCompat.getActionCount(notification)).isEqualTo(3)
  }

  @Test
  fun `a playing track offers pause`() {
    val notification = notificationFor(
      NotificationData(track = track, playerState = PlayerState.Playing)
    )

    val playPause = checkNotNull(NotificationCompat.getAction(notification, 1))
    assertThat(playPause.title.toString())
      .isEqualTo(app.getString(R.string.action_pause))
  }

  @Test
  fun `a paused track offers play`() {
    val notification = notificationFor(
      NotificationData(track = track, playerState = PlayerState.Paused)
    )

    val playPause = checkNotNull(NotificationCompat.getAction(notification, 1))
    assertThat(playPause.title.toString())
      .isEqualTo(app.getString(R.string.action_play))
  }

  @Test
  fun `the notification only alerts once so it does not buzz on every track`() {
    val notification = notificationFor(NotificationData(track = track))

    assertThat(notification.flags and Notification.FLAG_ONLY_ALERT_ONCE).isNotEqualTo(0)
  }

  @Test
  fun `the placeholder notification is public so it shows on the lock screen`() {
    val notification = builder.createPlaceholderBuilder().build()

    assertThat(notification.visibility).isEqualTo(NotificationCompat.VISIBILITY_PUBLIC)
  }
}
