package com.kelsos.mbrc.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import com.kelsos.mbrc.utilities.RxBus;
import roboguice.receiver.RoboAppWidgetProvider;

public class WidgetNormal extends RoboAppWidgetProvider {

  @Inject private Context context;
  @Inject private RxBus bus;

  private int[] widgetsIds;

  @Override public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    widgetsIds = appWidgetIds;

    try {
      bus.register(this, PlayStateChange.class, this::updatePlayState);
      bus.register(this, CoverChangedEvent.class, this::updateCover);
      bus.register(this, TrackInfoChangeEvent.class, this::updateDisplay);
    } catch (Exception ignore) {
      // It was already registered so ignore
    }

    for (int appWidgetId : appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      Intent intent = new Intent(context, BaseActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

      // Get the layout for the App Widget and attach an on-click listener
      // to the button
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_normal);

      views.setOnClickPendingIntent(R.id.widget_normal_image, pendingIntent);

      views.setOnClickPendingIntent(R.id.widget_normal_play,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, context));

      views.setOnClickPendingIntent(R.id.widget_normal_next,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, context));

      views.setOnClickPendingIntent(R.id.widget_normal_previous,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PREVIOUS, context));

      // Tell the AppWidgetManager to perform an load on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  public void updateDisplay(TrackInfoChangeEvent event) {

    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);

    final TrackInfo trackInfo = event.getTrackInfo();
    widget.setTextViewText(R.id.widget_normal_line_one, trackInfo.getTitle());
    widget.setTextViewText(R.id.widget_normal_line_two, trackInfo.getArtist());
    widget.setTextViewText(R.id.widget_normal_line_three, trackInfo.getAlbum());
    manager.updateAppWidget(widgetsIds, widget);
  }

  public void updateCover(CoverChangedEvent coverChangedEvent) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);
    if (coverChangedEvent.isAvailable()) {
      widget.setImageViewBitmap(R.id.widget_normal_image, coverChangedEvent.getCover());
    } else {
      widget.setImageViewResource(R.id.widget_normal_image, R.drawable.ic_image_no_cover);
    }
    manager.updateAppWidget(widgetsIds, widget);
  }

  public void updatePlayState(PlayStateChange state) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);
    final boolean isPlaying = PlayerState.PLAYING.equals(state.getState());
    widget.setImageViewResource(R.id.widget_normal_play,
        isPlaying ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    manager.updateAppWidget(widgetsIds, widget);
  }
}
