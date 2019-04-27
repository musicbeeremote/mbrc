package com.kelsos.mbrc.features.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.ui.navigation.player.PlayerFragment
import com.squareup.picasso.Picasso
import java.io.File
import kotlin.reflect.KClass

abstract class WidgetBase : AppWidgetProvider() {

  @LayoutRes
  abstract fun layout(): Int

  @DimenRes
  abstract fun imageSize(): Int

  @IdRes
  abstract fun imageId(): Int

  @IdRes
  abstract fun playButtonId(): Int

  abstract fun widgetClass(): KClass<out WidgetBase>

  private fun Bundle.isState() = getBoolean(WidgetUpdater.STATE, false)

  private fun Bundle.isInfo() = getBoolean(WidgetUpdater.INFO, false)

  private fun Bundle.isCover() = getBoolean(WidgetUpdater.COVER, false)

  private fun Bundle.cover() = getString(WidgetUpdater.COVER_PATH, "")

  @PlayerState.State
  private fun Bundle.state() = getString(WidgetUpdater.PLAYER_STATE, PlayerState.UNDEFINED)

  private fun Bundle.playingTrack(): PlayingTrack =
    getParcelable(WidgetUpdater.TRACK_INFO) ?: PlayingTrack()

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)

    val incomingIntent = intent ?: return
    if (incomingIntent.action != AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
      return
    }
    val extras = incomingIntent.extras ?: return
    val ctx = context ?: return
    updateWidget(ctx, extras)
  }

  private fun updateWidget(context: Context, extras: Bundle) {
    val widgetManager = AppWidgetManager.getInstance(context)
    val clazz = widgetClass().java
    val widgets = ComponentName(context.packageName, clazz.name)
    val widgetsIds = widgetManager.getAppWidgetIds(widgets)

    when {
      extras.isCover() -> {
        updateCover(context, widgetManager, widgetsIds, extras.cover())
      }
      extras.isInfo() -> updateInfo(
        context,
        widgetManager,
        widgetsIds,
        extras.playingTrack()
      )
      extras.isState() -> updatePlayState(
        context,
        widgetManager,
        widgetsIds,
        extras.state()
      )
    }
  }

  override fun onUpdate(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetIds: IntArray
  ) {
    super.onUpdate(context, appWidgetManager, appWidgetIds)

    for (appWidgetId in appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      val intent = Intent(context, PlayerFragment::class.java)
      val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

      // Get the layout for the App Widget and attach an on-click listener
      // to the button
      val views = RemoteViews(context.packageName, layout())

      setupActionIntents(views, pendingIntent, context)
      // Tell the AppWidgetManager to perform an set on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }

  abstract fun setupActionIntents(
    views: RemoteViews,
    pendingIntent: PendingIntent,
    context: Context
  )

  abstract fun setupTrackInfo(
    views: RemoteViews,
    info: PlayingTrack
  )

  private fun updateInfo(
    context: Context,
    widgetManager: AppWidgetManager,
    widgetsIds: IntArray,
    info: PlayingTrack
  ) {
    val views = RemoteViews(context.packageName, layout())
    setupTrackInfo(views, info)
    widgetManager.updateAppWidget(widgetsIds, views)
  }

  private fun updateCover(
    context: Context,
    widgetManager: AppWidgetManager,
    widgetsIds: IntArray,
    path: String
  ) {
    val widget = RemoteViews(context.packageName, layout())
    val coverFile = File(path)
    if (coverFile.exists()) {
      Picasso.get().invalidate(coverFile)
      Picasso.get().load(coverFile)
        .centerCrop()
        .resizeDimen(imageSize(), imageSize())
        .into(widget, imageId(), widgetsIds)
    } else {
      widget.setImageViewResource(imageId(), R.drawable.ic_image_no_cover)
      widgetManager.updateAppWidget(widgetsIds, widget)
    }
  }

  private fun updatePlayState(
    context: Context,
    manager: AppWidgetManager,
    widgetsIds: IntArray,
    @PlayerState.State state: String
  ) {
    val widget = RemoteViews(context.packageName, layout())

    widget.setImageViewResource(
      playButtonId(),
      if (PlayerState.PLAYING == state) {
        R.drawable.ic_action_pause
      } else {
        R.drawable.ic_action_play
      }
    )
    manager.updateAppWidget(widgetsIds, widget)
  }
}
