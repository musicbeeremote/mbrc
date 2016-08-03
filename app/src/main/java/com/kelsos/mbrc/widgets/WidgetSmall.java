package com.kelsos.mbrc.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.ui.activities.nav.MainActivity;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import javax.inject.Inject;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

public class WidgetSmall extends AppWidgetProvider {

  @Inject Context context;
  @Inject RxBus bus;

  private int[] widgetsIds;
  private Scope scope;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Timber.v("Update widget received");
    if (scope == null) {
      scope = Toothpick.openScope(context.getApplicationContext());
      Toothpick.inject(this, scope);
    }

    widgetsIds = appWidgetIds;

    try {
      bus.register(this, TrackInfoChangeEvent.class, this::updateDisplay);
      bus.register(this, CoverChangedEvent.class, this::updateCover);
      bus.register(this, PlayStateChange.class, this::updatePlayState);
    } catch (Exception ignore) {
      // It was already registered so ignore
    }

    for (int appWidgetId : appWidgetIds) {
      // Create an Intent to launch ExampleActivity
      Intent intent = new Intent(context, MainActivity.class);
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

  private void updateDisplay(TrackInfoChangeEvent event) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget = new RemoteViews(context.getPackageName(), R.layout.widget_small);
    TrackInfo info = event.getTrackInfo();
    smallWidget.setTextViewText(R.id.widget_small_line_one, info.title);
    smallWidget.setTextViewText(R.id.widget_small_line_two, info.artist);
    manager.updateAppWidget(widgetsIds, smallWidget);
  }

  private void updateCover(CoverChangedEvent coverAvailable) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget = new RemoteViews(context.getPackageName(), R.layout.widget_small);
    if (coverAvailable.isAvailable()) {
      smallWidget.setImageViewBitmap(R.id.widget_small_image, coverAvailable.getCover());
    } else {
      smallWidget.setImageViewResource(R.id.widget_small_image, R.drawable.ic_image_no_cover);
    }
    manager.updateAppWidget(widgetsIds, smallWidget);
  }

  private void updatePlayState(PlayStateChange state) {
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    final RemoteViews smallWidget = new RemoteViews(context.getPackageName(), R.layout.widget_small);

    smallWidget.setImageViewResource(R.id.widget_small_play,
        PlayerState.PLAYING.equals(state.getState()) ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    manager.updateAppWidget(widgetsIds, smallWidget);
  }
}
