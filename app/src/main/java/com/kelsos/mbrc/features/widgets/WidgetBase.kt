package com.kelsos.mbrc.features.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.RemoteViews
import androidx.annotation.IdRes
import coil.Coil
import coil.request.ImageRequest
import coil.target.Target
import com.kelsos.mbrc.NavigationActivity
import com.kelsos.mbrc.R
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.utilities.whenNotNull
import com.kelsos.mbrc.features.library.PlayingTrack
import java.io.File

abstract class WidgetBase : AppWidgetProvider() {
  abstract val config: WidgetConfig

  override fun onReceive(context: Context?, intent: Intent?) {
    super.onReceive(context, intent)
    if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
      whenNotNull(context, intent.extras, this::updateWidget)
    }
  }

  private fun updateWidget(context: Context, extras: Bundle) {
    val widgetManager = AppWidgetManager.getInstance(context)
    val widgets = ComponentName(context.packageName, config.widgetClass.java.name)
    val widgetsIds = widgetManager.getAppWidgetIds(widgets)
    val data = BundleData(extras)

    when {
      data.isCover() -> {
        updateCover(context, widgetManager, widgetsIds, data.cover())
      }
      data.isInfo() -> updateInfo(
        context,
        widgetManager,
        widgetsIds,
        data.playingTrack()
      )
      data.isState() -> updatePlayState(
        context,
        widgetManager,
        widgetsIds,
        data.state()
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
      val intent = Intent(context, NavigationActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
      )
      val views = RemoteViews(context.packageName, config.layout)
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
    val views = RemoteViews(context.packageName, config.layout)
    setupTrackInfo(views, info)
    widgetManager.updateAppWidget(widgetsIds, views)
  }

  private fun updateCover(
    context: Context,
    widgetManager: AppWidgetManager,
    widgetsIds: IntArray,
    path: String
  ) {
    val widget = RemoteViews(context.packageName, config.layout)
    val coverFile = File(path)
    if (coverFile.exists()) {
      val request = ImageRequest.Builder(context)
        .data(coverFile)
        .target(RemoteViewsTarget(config.imageId, widget))
        .build()
      Coil.imageLoader(context).enqueue(request)
    } else {
      widget.setImageViewResource(config.imageId, R.drawable.ic_image_no_cover)
      widgetManager.updateAppWidget(widgetsIds, widget)
    }
  }

  private fun updatePlayState(
    context: Context,
    manager: AppWidgetManager,
    widgetsIds: IntArray,
    state: PlayerState
  ) {
    val widget = RemoteViews(context.packageName, config.layout)

    widget.setImageViewResource(
      config.playButtonId,
      if (PlayerState.Playing == state) {
        R.drawable.ic_action_pause
      } else {
        R.drawable.ic_action_play
      }
    )
    manager.updateAppWidget(widgetsIds, widget)
  }
}

class RemoteViewsTarget(@IdRes private val id: Int, private val remoteViews: RemoteViews) : Target {

  override fun onStart(placeholder: Drawable?) = setDrawable(placeholder)

  override fun onError(error: Drawable?) = setDrawable(error)

  override fun onSuccess(result: Drawable) = setDrawable(result)

  private fun setDrawable(drawable: Drawable?) {
    remoteViews.setImageViewBitmap(id, (drawable as? BitmapDrawable)?.bitmap)
  }
}
