package com.kelsos.mbrc.messaging;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.services.RemoteSessionManager;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;

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
  private RemoteViews mNormalView;
  private RemoteViews mExpandedView;
  private Notification mNotification;
  @Inject private NotificationManager mNotificationManager;
  private Context context;
  private SettingsManager mSettings;
  private String previous;
  private String play;
  private String next;

  @Inject
  public NotificationService(Context context,
      RxBus bus,
      RemoteSessionManager sessionManager,
      SettingsManager mSettings) {
    this.context = context;
    this.sessionManager = sessionManager;
    this.mSettings = mSettings;
    bus.register(this, NotificationDataAvailable.class, this::handleNotificationData);
    previous = context.getString(R.string.notification_action_previous);
    play = context.getString(R.string.notification_action_play);
    next = context.getString(R.string.notification_action_next);
  }

  public void handleNotificationData(final NotificationDataAvailable event) {
    if (!mSettings.isNotificationControlEnabled()) {
      return;
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      notificationBuilder(event.getTitle(), event.getArtist(), event.getAlbum(), event.getCover(), event.getState());
    } else {
      buildLollipopNotification(event);
    }
  }

  /**
   * Creates an ongoing notification that displays the cover and information about the playing
   * track,
   * and also provides controls to skip or play/pause.
   *
   * @param title The title of the track playing.
   * @param artist The artist of the track playing.
   * @param cover The cover Bitmap.
   * @param state The current play state is used to display the proper play or pause icon.
   */
  @SuppressLint("NewApi") private void notificationBuilder(final String title,
      final String artist,
      final String album,
      final Bitmap cover,
      final String state) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mNormalView = new RemoteViews(context.getPackageName(), R.layout.ui_notification_control);
    updateNormalNotification(artist, title, cover);

    mBuilder.setContentIntent(getPendingIntent(OPEN, context));
    mNormalView.setOnClickPendingIntent(R.id.notification_play, getPendingIntent(PLAY, context));
    mNormalView.setOnClickPendingIntent(R.id.notification_next, getPendingIntent(NEXT, context));
    mNormalView.setOnClickPendingIntent(R.id.notification_close, getPendingIntent(CLOSE, context));

    mNotification = mBuilder.setSmallIcon(R.drawable.ic_mbrc_status).build();

    if (isJellyBean()) {
      mExpandedView = new RemoteViews(context.getPackageName(), R.layout.ui_notification_control_expanded);
      updateExpandedNotification(artist, title, album, cover);
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_playpause, getPendingIntent(PLAY, context));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_next, getPendingIntent(NEXT, context));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_previous, getPendingIntent(PREVIOUS, context));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_remove, getPendingIntent(CLOSE, context));
      mNotification.bigContentView = mExpandedView;
    }

    updatePlayState(state);

    mNotification.contentView = mNormalView;
    mNotification.flags = Notification.FLAG_ONGOING_EVENT;
    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, mNotification);
  }

  /**
   * Builds a {@link Notification.MediaStyle} style Notification to display.
   * Used only on {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
   *
   * @param event A notification event that the notification service received
   */
  @SuppressLint("NewApi") private void buildLollipopNotification(NotificationDataAvailable event) {
    int playStateIcon = PlayerState.PLAYING.equals(event.getState())
                        ? R.drawable.ic_action_pause
                        : R.drawable.ic_action_play;

    final Notification.MediaStyle mediaStyle = new Notification.MediaStyle();

    mediaStyle.setMediaSession((MediaSession.Token) sessionManager.getMediaSessionToken().getToken());

    Notification.Builder builder = new Notification.Builder(context).setVisibility(Notification.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .setStyle(mediaStyle.setShowActionsInCompactView(1, 2))
        .setContentTitle(event.getTitle())
        .setContentText(event.getArtist())
        .setSubText(event.getAlbum());

    if (Build.VERSION_CODES.M < Build.VERSION.SDK_INT) {
      //noinspection deprecation
      builder.addAction(R.drawable.ic_action_previous, previous, getPendingIntent(PREVIOUS, context))
          .addAction(playStateIcon, play, getPendingIntent(PLAY, context))
          .addAction(R.drawable.ic_action_next, next, getPendingIntent(NEXT, context));
    } else {
      Notification.Action previous = getPreviousAction();
      Notification.Action play = getPlayAction(playStateIcon);
      Notification.Action next = getNextAction();
      builder.addAction(previous).addAction(play).addAction(next);
    }

    builder.setContentIntent(getPendingIntent(OPEN, context));

    if (event.getCover() != null) {
      builder.setLargeIcon(event.getCover());
    } else {
      Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_image_no_cover);
      builder.setLargeIcon(icon);
    }

    mNotification = builder.build();

    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, mNotification);
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

  private void updateNormalNotification(final String artist, final String title, final Bitmap cover) {
    mNormalView.setTextViewText(R.id.notification_artist, artist);
    mNormalView.setTextViewText(R.id.notification_title, title);
    if (cover != null) {
      mNormalView.setImageViewBitmap(R.id.notification_album_art, cover);
    } else {
      mNormalView.setImageViewResource(R.id.notification_album_art, R.drawable.ic_image_no_cover);
    }
  }

  private boolean isJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  private void updateExpandedNotification(final String artist,
      final String title,
      final String album,
      final Bitmap cover) {
    mExpandedView.setTextViewText(R.id.expanded_notification_line_one, title);
    mExpandedView.setTextViewText(R.id.expanded_notification_line_two, artist);
    mExpandedView.setTextViewText(R.id.expanded_notification_line_three, album);

    if (cover != null) {
      mExpandedView.setImageViewBitmap(R.id.expanded_notification_cover, cover);
    } else {
      mExpandedView.setImageViewResource(R.id.expanded_notification_cover, R.drawable.ic_image_no_cover);
    }
  }

  private void updatePlayState(final String state) {
    if (mNormalView == null || mNotification == null) {
      return;
    }

    mNormalView.setImageViewResource(R.id.notification_play,
        PlayerState.PLAYING.equals(state) ? R.drawable.ic_action_pause : R.drawable.ic_action_play);

    if (isJellyBean() && mExpandedView != null) {

      mExpandedView.setImageViewResource(R.id.expanded_notification_playpause,
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
}
