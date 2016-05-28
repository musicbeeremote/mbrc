package com.kelsos.mbrc.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.ui.activities.BaseActivity;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.RoboGuice;
import timber.log.Timber;

public class WidgetNormal extends AppWidgetProvider {

  @Inject
  private Context context;
  @Inject
  private Bus bus;

  private int[] widgetsIds;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Timber.v("Update widget received");
    if (this.context == null) {
      RoboGuice.getInjector(context).injectMembers(this);
    }
    widgetsIds = appWidgetIds;

    try {
      bus.register(this);
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

      // Tell the AppWidgetManager to perform an update on the current app widget
      appWidgetManager.updateAppWidget(appWidgetId, views);
    }
  }

  @Subscribe
  public void updateDisplay(TrackInfoChange info) {

    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);

    widget.setTextViewText(R.id.widget_normal_line_one, info.title);
    widget.setTextViewText(R.id.widget_normal_line_two, info.artist);
    widget.setTextViewText(R.id.widget_normal_line_three, info.album);
    manager.updateAppWidget(widgetsIds, widget);
  }

  @Subscribe
  public void updateCover(CoverAvailable coverAvailable) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);
    if (coverAvailable.isAvailable()) {
      widget.setImageViewBitmap(R.id.widget_normal_image, coverAvailable.getCover());
    } else {
      widget.setImageViewResource(R.id.widget_normal_image, R.drawable.ic_image_no_cover);
    }
    manager.updateAppWidget(widgetsIds, widget);
  }

  @Subscribe
  public void updatePlayState(PlayStateChange state) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_normal);

    widget.setImageViewResource(R.id.widget_normal_play,
        state.getState() == PlayState.Playing ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    manager.updateAppWidget(widgetsIds, widget);
  }
}
