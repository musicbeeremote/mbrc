package com.kelsos.mbrc.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.ui.activities.MainFragmentActivity;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.receiver.RoboAppWidgetProvider;

public class WidgetSmall extends RoboAppWidgetProvider {

  @Inject private Context context;
  @Inject private Bus bus;

  private int[] widgetsIds;

  @Override public void onHandleUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {

    widgetsIds = appWidgetIds;

    try {
      bus.register(this);
    } catch (Exception ignore) {
      // It was already registered so ignore
    }

    for (int appWidgetId : appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      Intent intent = new Intent(context, MainFragmentActivity.class);
      PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

      // Get the layout for the App Widget and attach an on-click listener
      // to the button
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small);

      views.setOnClickPendingIntent(R.id.widget_small_image, pendingIntent);

      views.setOnClickPendingIntent(R.id.widget_small_play,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, context));

      views.setOnClickPendingIntent(R.id.widget_small_next,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, context));

      views.setOnClickPendingIntent(R.id.widget_small_previous,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PREVIOUS, context));

      // Tell the AppWidgetManager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  @Subscribe public void updateDisplay(TrackInfoChange info) {

    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget =
        new RemoteViews(context.getPackageName(), R.layout.widget_small);

    smallWidget.setTextViewText(R.id.widget_small_line_one, info.title);
    smallWidget.setTextViewText(R.id.widget_small_line_two, info.artist);
    manager.updateAppWidget(widgetsIds, smallWidget);
  }

  @Subscribe public void updateCover(CoverAvailable coverAvailable) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget =
        new RemoteViews(context.getPackageName(), R.layout.widget_small);
    if (coverAvailable.isAvailable()) {
      smallWidget.setImageViewBitmap(R.id.widget_small_image, coverAvailable.getCover());
    } else {
      smallWidget.setImageViewResource(R.id.widget_small_image, R.drawable.ic_image_no_cover);
    }
    manager.updateAppWidget(widgetsIds, smallWidget);
  }

  @Subscribe public void updatePlayState(PlayStateChange state) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget =
        new RemoteViews(context.getPackageName(), R.layout.widget_small);

    smallWidget.setImageViewResource(R.id.widget_small_play,
        state.getState() == PlayState.PLAYING ? R.drawable.ic_action_pause
            : R.drawable.ic_action_play);
    manager.updateAppWidget(widgetsIds, smallWidget);
  }
}
