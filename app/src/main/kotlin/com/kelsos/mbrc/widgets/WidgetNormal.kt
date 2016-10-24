package com.kelsos.mbrc.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import javax.inject.Inject
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.ui.activities.BaseActivity
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder
import com.kelsos.mbrc.utilities.RxBus
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import java.util.*

class WidgetNormal : AppWidgetProvider() {

  @Inject lateinit var context: Context
  @Inject lateinit var bus: RxBus

  private var widgetsIds: IntArray? = null

  private lateinit var scope: Scope

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    scope = Toothpick.openScope(context.applicationContext)
    this.context = context
    widgetsIds = appWidgetIds

    try {
      bus.register(this, PlayStateChange::class.java, { this.updatePlayState(it) })
      bus.register(this, CoverChangedEvent::class.java, { this.updateCover(it) })
      bus.register(this, TrackInfoChangeEvent::class.java, { this.updateDisplay(it) })
    } catch (ignore: Exception) {
      // It was already registered so ignore
    }

    for (appWidgetId in appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      val intent = Intent(context, BaseActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

      // Get the layout for the App Widget and attach an on-click listener
      // to the button
      val views = RemoteViews(context.packageName, R.layout.widget_normal)

      views.setOnClickPendingIntent(R.id.widget_normal_image, pendingIntent)

      views.setOnClickPendingIntent(R.id.widget_normal_play,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, context))

      views.setOnClickPendingIntent(R.id.widget_normal_next,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, context))

      views.setOnClickPendingIntent(R.id.widget_normal_previous,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PREVIOUS, context))

      // Tell the AppWidgetManager to perform an load on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  fun updateDisplay(event: TrackInfoChangeEvent) {

    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)

    val trackInfo = event.trackInfo
    widget.setTextViewText(R.id.widget_normal_line_one, trackInfo.title)
    widget.setTextViewText(R.id.widget_normal_line_two, trackInfo.artist)
    widget.setTextViewText(R.id.widget_normal_line_three, trackInfo.album)
    manager.updateAppWidget(widgetsIds, widget)
    Timber.i("[Normal] Updating info for widgets %s", Arrays.toString(widgetsIds))
  }

  fun updateCover(coverChangedEvent: CoverChangedEvent) {
    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)
    if (coverChangedEvent.isAvailable) {
      widget.setImageViewBitmap(R.id.widget_normal_image, coverChangedEvent.cover)
    } else {
      widget.setImageViewResource(R.id.widget_normal_image, R.drawable.ic_image_no_cover)
    }
    manager.updateAppWidget(widgetsIds, widget)
    Timber.i("[Normal] Updating cover for widgets %s", Arrays.toString(widgetsIds))
  }

  fun updatePlayState(state: PlayStateChange) {
    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)
    val isPlaying = PlayerState.PLAYING == state.state
    widget.setImageViewResource(R.id.widget_normal_play,
        if (isPlaying) R.drawable.ic_action_pause else R.drawable.ic_action_play)
    manager.updateAppWidget(widgetsIds, widget)
    Timber.i("[Normal] Updating state for widgets %s", Arrays.toString(widgetsIds))
  }
}
