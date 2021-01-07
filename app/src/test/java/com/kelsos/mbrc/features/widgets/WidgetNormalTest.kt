package com.kelsos.mbrc.features.widgets

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Looper.getMainLooper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowAppWidgetManager

@RunWith(AndroidJUnit4::class)
class WidgetNormalTest {
  private lateinit var appWidgetManager: AppWidgetManager
  private lateinit var shadowAppWidgetManager: ShadowAppWidgetManager
  private lateinit var contextWrapper: ContextWrapper

  private var widgetId: Int = -1
  private lateinit var widgetView: View

  private fun broadcastReceiver(value: (String) -> Unit): BroadcastReceiver {
    return object : BroadcastReceiver() {
      override fun onReceive(context: Context?, intent: Intent?) {
        value(checkNotNull(intent?.action))
      }
    }
  }

  @Before
  fun setUp() {
    val applicationContext = ApplicationProvider.getApplicationContext<Context>()
    contextWrapper = ContextWrapper(applicationContext)
    appWidgetManager = AppWidgetManager.getInstance(contextWrapper)
    shadowAppWidgetManager = shadowOf(appWidgetManager)
    widgetId = shadowAppWidgetManager.createWidget(WidgetNormal::class.java, R.layout.widget_normal)
    widgetView = shadowAppWidgetManager.getViewFor(widgetId)
    val provider = ComponentName(applicationContext.packageName, WidgetNormal::class.java.name)
    shadowAppWidgetManager.bindAppWidgetId(widgetId, provider)
  }

  @Test
  fun `widget should start empty`() {
    val lineOne = widgetView.findViewById<TextView>(R.id.widget_normal_line_one)
    val lineTwo = widgetView.findViewById<TextView>(R.id.widget_normal_line_two)
    val lineThree = widgetView.findViewById<TextView>(R.id.widget_normal_line_three)

    assertThat(lineOne.text).isEqualTo("")
    assertThat(lineTwo.text).isEqualTo("")
    assertThat(lineThree.text).isEqualTo("")
  }

  @Test
  fun `widget should update track info on broadcast`() {
    val updater = WidgetUpdaterImpl(contextWrapper)

    updater.updatePlayingTrack(
      PlayingTrack(
        artist = "Artist",
        title = "Title",
        album = "Album"
      )
    )

    shadowOf(getMainLooper()).idle()
    widgetView = shadowAppWidgetManager.getViewFor(widgetId)

    val lineOne = widgetView.findViewById<TextView>(R.id.widget_normal_line_one)
    val lineTwo = widgetView.findViewById<TextView>(R.id.widget_normal_line_two)
    val lineThree = widgetView.findViewById<TextView>(R.id.widget_normal_line_three)

    assertThat(lineOne.text).isEqualTo("Title")
    assertThat(lineTwo.text).isEqualTo("Artist")
    assertThat(lineThree.text).isEqualTo("Album")
  }

  @Test
  fun `widget should update play state on broadcast`() {
    val updater = WidgetUpdaterImpl(contextWrapper)
    val before = widgetView.findViewById<ImageView>(R.id.widget_normal_play)

    updater.updatePlayState(PlayerState.Playing)
    shadowOf(getMainLooper()).idle()

    widgetView = shadowAppWidgetManager.getViewFor(widgetId)
    val after = widgetView.findViewById<ImageView>(R.id.widget_normal_play)

    assertThat(before.drawable).isNotEqualTo(after.drawable)
  }

  @Test
  fun `pressing the play button should broadcast and intent`() {
    val button = widgetView.findViewById<ImageView>(R.id.widget_normal_play)
    var action = ""
    val intentFilter = IntentFilter(RemoteViewIntentBuilder.PLAY_PRESSED)
    val receiver = broadcastReceiver { action = it }
    contextWrapper.registerReceiver(receiver, intentFilter)
    button.performClick()
    shadowOf(getMainLooper()).idle()
    assertThat(action).isEqualTo(RemoteViewIntentBuilder.PLAY_PRESSED)
  }

  @Test
  fun `pressing the next button should broadcast an intent`() {
    val button = widgetView.findViewById<ImageView>(R.id.widget_normal_next)
    var action = ""
    val intentFilter = IntentFilter(RemoteViewIntentBuilder.NEXT_PRESSED)
    val receiver = broadcastReceiver { action = it }
    contextWrapper.registerReceiver(receiver, intentFilter)
    button.performClick()
    shadowOf(getMainLooper()).idle()
    assertThat(action).isEqualTo(RemoteViewIntentBuilder.NEXT_PRESSED)
  }

  @Test
  fun `pressing the previous button should broadcast and intent`() {
    val button = widgetView.findViewById<ImageView>(R.id.widget_normal_previous)
    var action = ""
    val intentFilter = IntentFilter(RemoteViewIntentBuilder.PREVIOUS_PRESSED)
    val receiver = broadcastReceiver { action = it }
    contextWrapper.registerReceiver(receiver, intentFilter)
    button.performClick()
    shadowOf(getMainLooper()).idle()
    assertThat(action).isEqualTo(RemoteViewIntentBuilder.PREVIOUS_PRESSED)
  }
}
