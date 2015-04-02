package com.kelsos.mbrc.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.ui.activities.HomeActivity;

@Singleton public class NotificationService {
  public static final int PLUGIN_OUT_OF_DATE = 15612;
  public static final int NOW_PLAYING_PLACEHOLDER = 15613;
  public static final int SYNC_UPDATE = 0x1258;
  public static final String NOTIFICATION_PLAY_PRESSED = "com.kelsos.mbrc.notification.play";
  public static final String NOTIFICATION_NEXT_PRESSED = "com.kelsos.mbrc.notification.next";
  public static final String NOTIFICATION_CLOSE_PRESSED = "com.kelsos.mbrc.notification.close";
  public static final String NOTIFICATION_PREVIOUS_PRESSED =
      "com.kelsos.mbrc.notification.previous";

  private static final int OPEN = 0;
  private static final int PLAY = 1;
  private static final int NEXT = 2;
  private static final int CLOSE = 3;
  private static final int PREVIOUS = 4;

  private RemoteViews mNormalView;
  private RemoteViews mExpandedView;
  private Notification mNotification;
  private NotificationManager mNotificationManager;
  private Context mContext;
  private SettingsManager mSettings;

  @Inject public NotificationService(Context context, SettingsManager mSettings,
      NotificationManager mNotificationManager) {
    this.mContext = context;
    this.mSettings = mSettings;
    this.mNotificationManager = mNotificationManager;
  }

  public void handleNotificationData(final NotificationDataAvailable event) {
    if (!mSettings.isNotificationControlEnabled()) {
      return;
    }
    notificationBuilder(event.getTitle(), event.getArtist(), event.getAlbum(), event.getState(),
        event.getCover());
  }

  private boolean atLeastJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  private void updateNormalNotification(final String artist, final String title, Bitmap cover) {
    mNormalView.setTextViewText(R.id.notification_artist, artist);
    mNormalView.setTextViewText(R.id.notification_title, title);
    mNormalView.setImageViewBitmap(R.id.notification_album_art, cover);
  }

  private void updateExpandedNotification(final String artist, final String title,
      final String album, Bitmap cover) {
    mExpandedView.setTextViewText(R.id.expanded_notification_line_one, title);
    mExpandedView.setTextViewText(R.id.expanded_notification_line_two, artist);
    mExpandedView.setTextViewText(R.id.expanded_notification_line_three, album);
    mExpandedView.setImageViewBitmap(R.id.expanded_notification_cover, cover);
  }

  /**
   * Creates an ongoing notification that displays the cover and information about the playing
   * track,
   * and also provides controls to skip or play/pause.
   *
   * @param title The title of the track playing.
   * @param artist The artist of the track playing.
   * @param state The current play state is used to display the proper play or pause icon.
   */
  @SuppressLint("NewApi") private void notificationBuilder(final String title, final String artist,
      final String album, final PlayState state, Bitmap cover) {

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
    mNormalView = new RemoteViews(mContext.getPackageName(), R.layout.ui_notification_control);

    mBuilder.setContentIntent(getPendingIntent(OPEN));
    mNormalView.setOnClickPendingIntent(R.id.notification_play, getPendingIntent(PLAY));
    mNormalView.setOnClickPendingIntent(R.id.notification_next, getPendingIntent(NEXT));
    mNormalView.setOnClickPendingIntent(R.id.notification_close, getPendingIntent(CLOSE));

    mNotification = mBuilder.build();
    updateNormalNotification(artist, title, cover);
    if (atLeastJellyBean()) {
      mExpandedView =
          new RemoteViews(mContext.getPackageName(), R.layout.ui_notification_control_expanded);
      updateExpandedNotification(artist, title, album, cover);
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_playpause,
          getPendingIntent(PLAY));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_next,
          getPendingIntent(NEXT));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_previous,
          getPendingIntent(PREVIOUS));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_remove,
          getPendingIntent(CLOSE));
      mNotification.bigContentView = mExpandedView;
    }

    updatePlayState(state);

    mNotification.contentView = mNormalView;
    mNotification.flags = Notification.FLAG_ONGOING_EVENT;
    mNotification.icon = R.drawable.ic_mbrc_status;
    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, mNotification);
  }

  private void updatePlayState(final PlayState state) {
    if (mNormalView == null || mNotification == null) {
      return;
    }

    mNormalView.setImageViewResource(R.id.notification_play,
        state == PlayState.PLAYING ? R.drawable.ic_action_pause : R.drawable.ic_action_play);

    if (atLeastJellyBean() && mExpandedView != null) {

      mExpandedView.setImageViewResource(R.id.expanded_notification_playpause,
          state == PlayState.PLAYING ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    }
  }

  private PendingIntent getPendingIntent(int id) {
    switch (id) {
      case OPEN:
        Intent notificationIntent = new Intent(mContext, HomeActivity.class);
        return PendingIntent.getActivity(mContext, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
      case PLAY:
        Intent playPressedIntent = new Intent(NOTIFICATION_PLAY_PRESSED);
        return PendingIntent.getBroadcast(mContext, 1, playPressedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
      case NEXT:
        Intent mediaNextButtonIntent = new Intent(NOTIFICATION_NEXT_PRESSED);
        return PendingIntent.getBroadcast(mContext, 2, mediaNextButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
      case CLOSE:
        Intent clearNotificationIntent = new Intent(NOTIFICATION_CLOSE_PRESSED);
        return PendingIntent.getBroadcast(mContext, 3, clearNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
      case PREVIOUS:
        Intent mediaPreviousButtonIntent = new Intent(NOTIFICATION_PREVIOUS_PRESSED);
        return PendingIntent.getBroadcast(mContext, 4, mediaPreviousButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void cancelNotification(final int notificationId) {
    mNotificationManager.cancel(notificationId);
  }

  public void updateAvailableNotificationBuilder() {
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_mbrc_status)
            .setContentTitle(mContext.getString(R.string.application_name))
            .setContentText(mContext.getString(R.string.notification_plugin_out_of_date));

    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    resultIntent.setData(Uri.parse("http://kelsos.net/musicbeeremote/download/"));

    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    mBuilder.setContentIntent(resultPendingIntent);
    final Notification notification = mBuilder.build();
    notification.flags = Notification.FLAG_AUTO_CANCEL;
    mNotificationManager.notify(PLUGIN_OUT_OF_DATE, notification);
  }

  public void librarySyncNotification(final int total, final int current) {
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
    mBuilder.setContentTitle(mContext.getString(R.string.mbrc_library_sync))
        .setContentText(mContext.getString(R.string.mbrc_libary_sync_msg))
        .setSmallIcon(R.drawable.ic_mbrc_status);

    if (current == total) {
      mBuilder.setContentText(mContext.getString(R.string.mbrc_library_sync_complete))
          .setProgress(0, 0, false);
    } else {
      mBuilder.setContentText(
          String.format(mContext.getString(R.string.mbrc_libary_sync_msg), current, total))
          .setProgress(total, current, false);
    }
    mNotificationManager.notify(SYNC_UPDATE, mBuilder.build());
  }
}
