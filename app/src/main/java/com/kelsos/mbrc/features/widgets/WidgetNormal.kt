package com.kelsos.mbrc.features.widgets

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import com.kelsos.mbrc.R
import com.kelsos.mbrc.features.library.tracks.PlayingTrack
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.NEXT
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PLAY
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.PREVIOUS
import com.kelsos.mbrc.platform.mediasession.RemoteViewIntentBuilder.getPendingIntent
import kotlin.reflect.KClass

class WidgetNormal : WidgetBase() {
  override fun layout(): Int = R.layout.widget_normal

  override fun imageSize(): Int = R.dimen.widget_normal_height

  override fun imageId(): Int = R.id.widget_normal_image

  override fun playButtonId(): Int = R.id.widget_normal_play

  override fun widgetClass(): KClass<out WidgetBase> = WidgetNormal::class

  override fun setupActionIntents(
    views: RemoteViews,
    pendingIntent: PendingIntent,
    context: Context
  ) {
    views.setOnClickPendingIntent(R.id.widget_normal_image, pendingIntent)
    views.setOnClickPendingIntent(R.id.widget_normal_play, getPendingIntent(PLAY, context))
    views.setOnClickPendingIntent(R.id.widget_normal_next, getPendingIntent(NEXT, context))
    views.setOnClickPendingIntent(
      R.id.widget_normal_previous,
      getPendingIntent(PREVIOUS, context)
    )
  }

  override fun setupTrackInfo(views: RemoteViews, info: PlayingTrack) {
    views.setTextViewText(R.id.widget_normal_line_one, info.title)
    views.setTextViewText(R.id.widget_normal_line_two, info.artist)
    views.setTextViewText(R.id.widget_normal_line_three, info.album)
  }
}
