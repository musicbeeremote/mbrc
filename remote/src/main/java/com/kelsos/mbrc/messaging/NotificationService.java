package com.kelsos.mbrc.messaging;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.services.RemoteSessionManager;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import roboguice.util.Ln;

import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.CLOSE;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.NEXT;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.OPEN;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PLAY;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.PREVIOUS;
import static com.kelsos.mbrc.utilities.RemoteViewIntentBuilder.getPendingIntent;

@Singleton public class NotificationService {
  public static final int PLUGIN_OUT_OF_DATE = 15612;
  public static final int NOW_PLAYING_PLACEHOLDER = 15613;
  public static final int SYNC_UPDATE = 0x1258;
  private final RemoteSessionManager sessionManager;
  private RemoteViews normalView;
  private RemoteViews expandedView;
  private Notification notification;
  @Inject private NotificationManager mNotificationManager;
  private Context context;
  private SettingsManager settings;
  private String previous;
  private String play;
  private String next;
  private Notification.Builder lollipopNotification;

  @Inject
  public NotificationService(Context context,
      RxBus bus,
      RemoteSessionManager sessionManager,
      SettingsManager mSettings) {
    this.context = context;
    this.sessionManager = sessionManager;
    this.settings = mSettings;
    bus.register(this, TrackInfoChangeEvent.class, this::handleTrackInfo);
    bus.register(this, CoverChangedEvent.class, this::coverChanged);
    bus.register(this, PlayStateChange.class, this::playStateChanged);
    bus.register(this, ConnectionStatusChangeEvent.class, this::connectionChanged);
    previous = context.getString(R.string.notification_action_previous);
    play = context.getString(R.string.notification_action_play);
    next = context.getString(R.string.notification_action_next);
  }

  private void handleTrackInfo(TrackInfoChangeEvent event) {
    TrackInfo info = event.getTrackInfo();
    if (isLollipop()) {
      Notification.Builder builder;
      if (lollipopNotification != null) {
        builder = lollipopNotification;
      } else {
        builder = new Notification.Builder(context);
      }

      builder.setContentTitle(info.getTitle()).setContentText(info.getArtist()).setSubText(info.getAlbum());
      notification = builder.build();
    } else {
      updateTrackInfo(info.getArtist(), info.getTitle(), info.getAlbum());
    }

    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void coverChanged(CoverChangedEvent event) {
    if (isLollipop()) {
      Notification.Builder builder;
      if (lollipopNotification != null) {
        builder = lollipopNotification;
      } else {
        builder = new Notification.Builder(context);
      }

      if (event.getCover() != null) {
        builder.setLargeIcon(event.getCover());
      }

      notification = builder.build();
    } else {
      updateCover(event.getCover());
    }

    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void playStateChanged(PlayStateChange event) {
    if (isLollipop()) {
      Notification.Builder builder;
      if (lollipopNotification != null) {
        builder = lollipopNotification;
      } else {
        builder = new Notification.Builder(context);
      }

      clearActions();

      int resId = event.getState().equals(PlayerState.PLAYING) ? R.drawable.ic_action_pause : R.drawable.ic_action_play;
      addActions(builder, resId);
      notification = builder.build();
    } else {
      updatePlayState(event.getState());
    }

    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
  }

  private void connectionChanged(ConnectionStatusChangeEvent event) {
    if (!settings.isNotificationControlEnabled()) {
      Ln.v("Notification is off doing nothing");
      return;
    }

    if (event.getStatus() == Connection.OFF) {
      mNotificationManager.cancel(NOW_PLAYING_PLACEHOLDER);
    }

    if (isLollipop()) {
      lollipopNotification = buildLollipopNotification();
    } else {
      buildNotification();
    }
  }

  private boolean isLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  /**
   * Creates an ongoing notification that displays the cover and information about the playing
   * track,
   * and also provides controls to skip or play/pause.
   */
  @SuppressLint("NewApi") private void buildNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

    normalView = new RemoteViews(context.getPackageName(), R.layout.ui_notification_control);
    builder.setContentIntent(getPendingIntent(OPEN, context));
    normalView.setOnClickPendingIntent(R.id.notification_play, getPendingIntent(PLAY, context));
    normalView.setOnClickPendingIntent(R.id.notification_next, getPendingIntent(NEXT, context));
    normalView.setOnClickPendingIntent(R.id.notification_close, getPendingIntent(CLOSE, context));

    notification = builder.setSmallIcon(R.drawable.ic_mbrc_status).build();

    if (isJellyBean()) {
      expandedView = new RemoteViews(context.getPackageName(), R.layout.ui_notification_control_expanded);
      expandedView.setOnClickPendingIntent(R.id.expanded_notification_playpause, getPendingIntent(PLAY, context));
      expandedView.setOnClickPendingIntent(R.id.expanded_notification_next, getPendingIntent(NEXT, context));
      expandedView.setOnClickPendingIntent(R.id.expanded_notification_previous, getPendingIntent(PREVIOUS, context));
      expandedView.setOnClickPendingIntent(R.id.expanded_notification_remove, getPendingIntent(CLOSE, context));
      notification.bigContentView = expandedView;
    }

    notification.contentView = normalView;
    notification.flags = Notification.FLAG_ONGOING_EVENT;
  }

  /**
   * Builds a {@link Notification.MediaStyle} style Notification to display.
   * Used only on {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
   */
  @SuppressLint("NewApi") private Notification.Builder buildLollipopNotification() {
    final Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
    mediaStyle.setMediaSession((MediaSession.Token) sessionManager.getMediaSessionToken().getToken());

    Notification.Builder builder = new Notification.Builder(context).setVisibility(Notification.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setStyle(mediaStyle.setShowActionsInCompactView(1, 2));

    addActions(builder, R.drawable.ic_action_play);

    builder.setContentIntent(getPendingIntent(OPEN, context));
    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_image_no_cover);
    builder.setLargeIcon(icon);

    return builder;
  }

  @TargetApi(Build.VERSION_CODES.KITKAT_WATCH) private void addActions(Notification.Builder builder, int resId) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      //noinspection deprecation
      builder.addAction(R.drawable.ic_action_previous, previous, getPendingIntent(PREVIOUS, context))
          .addAction(resId, play, getPendingIntent(PLAY, context))
          .addAction(R.drawable.ic_action_next, next, getPendingIntent(NEXT, context));
    } else {
      Notification.Action previous = getPreviousAction();
      Notification.Action play = getPlayAction(resId);
      Notification.Action next = getNextAction();
      builder.addAction(previous).addAction(play).addAction(next);
    }
  }

  @TargetApi(Build.VERSION_CODES.M) private Notification.Action getPreviousAction() {
    PendingIntent previousIntent = getPendingIntent(PREVIOUS, context);
    Icon previousIcon = Icon.createWithResource(context, R.drawable.ic_action_previous);
    return new Notification.Action.Builder(previousIcon, previous, previousIntent).build();
  }

  @TargetApi(Build.VERSION_CODES.M) private Notification.Action getPlayAction(int playStateIcon) {
    PendingIntent playIntent = getPendingIntent(PLAY, context);
    Icon playIcon = Icon.createWithResource(context, playStateIcon);
    return new Notification.Action.Builder(playIcon, play, playIntent).build();
  }

  @TargetApi(Build.VERSION_CODES.M) private Notification.Action getNextAction() {
    PendingIntent nextIntent = getPendingIntent(NEXT, context);
    Icon nextIcon = Icon.createWithResource(context, R.drawable.ic_action_next);
    return new Notification.Action.Builder(nextIcon, next, nextIntent).build();
  }

  private void updateTrackInfo(final String artist, final String title, final String album) {
    normalView.setTextViewText(R.id.notification_artist, artist);
    normalView.setTextViewText(R.id.notification_title, title);

    if (!isJellyBean()) {
      return;
    }

    expandedView.setTextViewText(R.id.expanded_notification_line_one, title);
    expandedView.setTextViewText(R.id.expanded_notification_line_two, artist);
    expandedView.setTextViewText(R.id.expanded_notification_line_three, album);
  }

  private void updateCover(Bitmap cover) {
    if (cover != null) {
      normalView.setImageViewBitmap(R.id.notification_album_art, cover);
    } else {
      normalView.setImageViewResource(R.id.notification_album_art, R.drawable.ic_image_no_cover);
    }

    if (!isJellyBean()) {
      return;
    }

    if (cover != null) {
      expandedView.setImageViewBitmap(R.id.expanded_notification_cover, cover);
    } else {
      expandedView.setImageViewResource(R.id.expanded_notification_cover, R.drawable.ic_image_no_cover);
    }
  }

  private boolean isJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  private void updatePlayState(final String state) {
    if (normalView == null || notification == null) {
      return;
    }

    normalView.setImageViewResource(R.id.notification_play,
        PlayerState.PLAYING.equals(state) ? R.drawable.ic_action_pause : R.drawable.ic_action_play);

    if (isJellyBean() && expandedView != null) {

      expandedView.setImageViewResource(R.id.expanded_notification_playpause,
          PlayerState.PLAYING.equals(state) ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    }
  }

  public void cancelNotification(final int notificationId) {
    mNotificationManager.cancel(notificationId);
  }

  @SuppressLint("NewApi") public void updateAvailableNotificationBuilder() {
    NotificationCompat.Builder
        mBuilder
        = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_mbrc_status)
        .setContentTitle(context.getString(R.string.application_name))
        .setContentText(context.getString(R.string.notification_plugin_out_of_date));

    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    } else {
      resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    resultIntent.setData(Uri.parse("http://kelsos.net/musicbeeremote/download/"));

    PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
        0,
        resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    mBuilder.setContentIntent(resultPendingIntent);
    final Notification notification = mBuilder.build();
    notification.flags = Notification.FLAG_AUTO_CANCEL;
    mNotificationManager.notify(PLUGIN_OUT_OF_DATE, notification);
  }

  public void librarySyncNotification(final int total, final int current) {
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mBuilder.setContentTitle(context.getString(R.string.mbrc_library_sync))
        .setContentText(context.getString(R.string.mbrc_libary_sync_msg))
        .setSmallIcon(R.drawable.ic_mbrc_status);

    if (current == total) {
      mBuilder.setContentText(context.getString(R.string.mbrc_library_sync_complete)).setProgress(0, 0, false);
    } else {
      mBuilder.setContentText(String.format(context.getString(R.string.mbrc_libary_sync_msg), current, total))
          .setProgress(total, current, false);
    }
    mNotificationManager.notify(SYNC_UPDATE, mBuilder.build());
  }

  private void clearActions() {
    final RemoteViews big = notification.bigContentView;
    if (big != null) {
      final Resources res = Resources.getSystem();
      final int android_R_id_actions = res.getIdentifier("actions", "id", "android");
      final int android_R_id_action_divider = res.getIdentifier("action_divider", "id", "android");
      if (android_R_id_actions != 0 && android_R_id_action_divider != 0) {
        big.removeAllViews(android_R_id_actions);
        big.setViewVisibility(android_R_id_actions, View.GONE);
        big.setViewVisibility(android_R_id_action_divider, View.GONE);
      }
    }
  }
}
