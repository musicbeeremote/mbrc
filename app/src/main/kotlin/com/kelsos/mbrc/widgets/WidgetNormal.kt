package com.kelsos.mbrc.widgets

import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.kelsos.mbrc.R
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.ui.activities.nav.MainActivity
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject

class WidgetNormal : AppWidgetProvider() {

  @Inject lateinit var context: Application
  @Inject lateinit var bus: RxBus

  private var widgetsIds: IntArray? = null
  private var scope: Scope? = null

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)
    Timber.v("Update widget received")
    if (scope == null) {
      scope = Toothpick.openScope(context.applicationContext)
      Toothpick.inject(this, scope)
    }
    widgetsIds = appWidgetIds

    bus.register(this, TrackInfoChangeEvent::class.java, { this.updateDisplay(it) })
    bus.register(this, CoverChangedEvent::class.java, { this.updateCover(it) })
    bus.register(this, PlayStateChange::class.java, { this.updatePlayState(it) })

    for (appWidgetId in appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      val intent = Intent(context, MainActivity::class.java)
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

      // Tell the AppWidgetManager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  private fun updateDisplay(event: TrackInfoChangeEvent) {
    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)
    val info = event.trackInfo
    widget.setTextViewText(R.id.widget_normal_line_one, info.title)
    widget.setTextViewText(R.id.widget_normal_line_two, info.artist)
    widget.setTextViewText(R.id.widget_normal_line_three, info.album)
    manager.updateAppWidget(widgetsIds, widget)
  }

  private fun updateCover(coverAvailable: CoverChangedEvent) {
    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)
    if (coverAvailable.isAvailable) {
      widget.setImageViewBitmap(R.id.widget_normal_image, coverAvailable.cover)
    } else {
      widget.setImageViewResource(R.id.widget_normal_image, R.drawable.ic_image_no_cover)
    }
    manager.updateAppWidget(widgetsIds, widget)
  }

  private fun updatePlayState(state: PlayStateChange) {
    val manager = AppWidgetManager.getInstance(context)
    val widget = RemoteViews(context.packageName, R.layout.widget_normal)

    widget.setImageViewResource(R.id.widget_normal_play,
        if (PlayerState.PLAYING == state.state) R.drawable.ic_action_pause else R.drawable.ic_action_play)
    manager.updateAppWidget(widgetsIds, widget)
  }
}
