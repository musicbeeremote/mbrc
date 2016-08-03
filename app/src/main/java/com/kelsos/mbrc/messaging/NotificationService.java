package com.kelsos.mbrc.messaging;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.model.NotificationModel;
import com.kelsos.mbrc.services.RemoteSessionManager;
import com.kelsos.mbrc.utilities.SettingsManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.NEXT;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.OPEN;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PLAY;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PREVIOUS;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.getPendingIntent;

@Singleton
public class NotificationService {
  public static final int NOW_PLAYING_PLACEHOLDER = 15613;
  private final RemoteSessionManager sessionManager;
  private Notification notification;
  @Inject NotificationManagerCompat notificationManager;
  @Inject NotificationModel model;
  private Context context;
  private SettingsManager settings;
  private String previous;
  private String play;
  private String next;

  @Inject
  public NotificationService(Context context,
      RxBus bus,
      RemoteSessionManager sessionManager,
      SettingsManager settings) {
    this.context = context;
    this.sessionManager = sessionManager;
    this.settings = settings;
    bus.register(this, TrackInfoChangeEvent.class, this::handleTrackInfo);
    bus.register(this, CoverChangedEvent.class, this::coverChanged);
    bus.register(this, PlayStateChange.class, this::playStateChanged);
    bus.register(this, ConnectionStatusChangeEvent.class, this::connectionChanged);
    previous = context.getString(R.string.notification_action_previous);
    play = context.getString(R.string.notification_action_play);
    next = context.getString(R.string.notification_action_next);
  }

  private void handleTrackInfo(TrackInfoChangeEvent event) {
    model.setTrackInfo(event.getTrackInfo());
    notification = createBuilder().build();
    notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void coverChanged(CoverChangedEvent event) {
    model.setCover(event.getCover());
    notification = createBuilder().build();
    notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void playStateChanged(PlayStateChange event) {
    model.setPlayState(event.getState());
    notification = createBuilder().build();
    notificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void connectionChanged(ConnectionStatusChangeEvent event) {
    if (!settings.isNotificationControlEnabled()) {
      Timber.v("Notification is off doing nothing");
      return;
    }

    if (event.getStatus() == Connection.OFF) {
      notificationManager.cancel(NOW_PLAYING_PLACEHOLDER);
    }

    notification = createBuilder().build();
  }

  private NotificationCompat.Builder createBuilder() {
    final NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle();
    mediaStyle.setMediaSession(sessionManager.getMediaSessionToken());

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
    int resId =
        model.getPlayState().equals(PlayerState.PLAYING) ? R.drawable.ic_action_pause : R.drawable.ic_action_play;

    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setStyle(mediaStyle.setShowActionsInCompactView(1, 2))
        .addAction(getPreviousAction())
        .addAction(getPlayAction(resId))
        .addAction(getNextAction());

    if (model.getCover() != null) {
      builder.setLargeIcon(model.getCover());
    } else {
      Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_image_no_cover);
      builder.setLargeIcon(icon);
    }

    TrackInfo info = model.getTrackInfo();

    if (info != null) {
      builder.setContentTitle(info.getTitle()).setContentText(info.getArtist()).setSubText(info.getAlbum());
    }

    builder.setContentIntent(getPendingIntent(OPEN, context));

    return builder;
  }

  @NonNull
  private NotificationCompat.Action getPreviousAction() {
    PendingIntent previousIntent = getPendingIntent(PREVIOUS, context);
    return new NotificationCompat.Action.Builder(R.drawable.ic_action_previous, previous, previousIntent).build();
  }

  @NonNull
  private NotificationCompat.Action getPlayAction(int playStateIcon) {
    PendingIntent playIntent = getPendingIntent(PLAY, context);

    return new NotificationCompat.Action.Builder(playStateIcon, play, playIntent).build();
  }

  @NonNull
  private NotificationCompat.Action getNextAction() {
    PendingIntent nextIntent = getPendingIntent(NEXT, context);
    return new NotificationCompat.Action.Builder(R.drawable.ic_action_next, next, nextIntent).build();
  }

  public void cancelNotification(final int notificationId) {
    notificationManager.cancel(notificationId);
  }
}
