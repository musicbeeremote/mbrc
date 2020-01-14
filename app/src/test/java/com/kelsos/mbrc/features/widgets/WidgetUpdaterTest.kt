package com.kelsos.mbrc.features.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Looper.getMainLooper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.tracks.PlayingTrack
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class WidgetUpdaterTest {

  @get:Rule
  val rule = InstantTaskExecutorRule()

  private lateinit var widgetUpdater: WidgetUpdater
  private lateinit var contextWrapper: ContextWrapper
  private val context: Context = ApplicationProvider.getApplicationContext()

  val flags = mutableListOf<Boolean>()
  val names = mutableListOf<String>()

  val classes = listOf(
    "com.kelsos.mbrc.features.widgets.WidgetSmall",
    "com.kelsos.mbrc.features.widgets.WidgetNormal"
  )

  @Before
  fun setUp() {
    contextWrapper = ContextWrapper(context)
    widgetUpdater = WidgetUpdaterImpl(contextWrapper)
  }

  private fun broadcastReceiver(flag: String, value: (Bundle) -> Unit): BroadcastReceiver {
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras ?: return
        flags.add(bundle.getBoolean(flag, false))
        value(bundle)
        names.add(intent.component?.className ?: "")
      }
    }
  }

  private val intentFilter = IntentFilter(AppWidgetManager.ACTION_APPWIDGET_UPDATE)

  @Test
  fun `should broadcast two similar intents when updating cover`() {
    val values = mutableListOf<String>()

    val broadcastReceiver = broadcastReceiver(WidgetUpdater.COVER) { bundle ->
      values.add(bundle.getString(WidgetUpdater.COVER_PATH, ""))
    }

    contextWrapper.registerReceiver(broadcastReceiver, intentFilter)
    widgetUpdater.updateCover("/tmp/image.png")
    shadowOf(getMainLooper()).idle()

    assertThat(flags).containsExactly(true, true)
    assertThat(values).containsExactly("/tmp/image.png", "/tmp/image.png")
    assertThat(names).containsExactlyElementsIn(classes)
  }

  @Test
  fun `should broadcast two similar intent when updating play state`() {
    val values = mutableListOf<String>()

    val broadcastReceiver = broadcastReceiver(WidgetUpdater.STATE) { bundle ->
      values.add(bundle.getString(WidgetUpdater.PLAYER_STATE, ""))
    }

    contextWrapper.registerReceiver(broadcastReceiver, intentFilter)
    widgetUpdater.updatePlayState(PlayerState.STOPPED)
    shadowOf(getMainLooper()).idle()

    assertThat(flags).containsExactly(true, true)
    assertThat(values).containsExactly(PlayerState.STOPPED, PlayerState.STOPPED)
    assertThat(names).containsExactlyElementsIn(classes)
  }

  @Test
  fun `should broadcast two similar intents when updating track info`() {
    val values = mutableListOf<PlayingTrack>()
    val track = PlayingTrack(
      artist = "Test Artist",
      title = "Test Track",
      album = "Test Album"
    )

    val broadcastReceiver = broadcastReceiver(WidgetUpdater.INFO) { bundle ->
      values.add(checkNotNull(bundle.getParcelable(WidgetUpdater.TRACK_INFO)))
    }

    contextWrapper.registerReceiver(broadcastReceiver, intentFilter)
    widgetUpdater.updatePlayingTrack(track)
    shadowOf(getMainLooper()).idle()

    assertThat(flags).containsExactly(true, true)
    assertThat(values).containsExactly(track, track)
    assertThat(names).containsExactlyElementsIn(classes)
  }
}
