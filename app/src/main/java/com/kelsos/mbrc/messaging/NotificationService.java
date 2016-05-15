package com.kelsos.mbrc.messaging;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.services.RemoteSessionManager;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.squareup.otto.Subscribe;

@Singleton public class NotificationService {
  public static final int PLUGIN_OUT_OF_DATE = 15612;
  public static final int NOW_PLAYING_PLACEHOLDER = 15613;
  private final RemoteSessionManager sessionManager;
  private RemoteViews mNormalView;
  private RemoteViews mExpandedView;
  private Notification mNotification;
  private NotificationManager mNotificationManager;
  private Context mContext;
  private SettingsManager mSettings;

  @Inject
  public NotificationService(Context context,
      MainThreadBusWrapper bus,
      RemoteSessionManager sessionManager,
      SettingsManager mSettings) {
    this.mContext = context;
    this.sessionManager = sessionManager;
    this.mSettings = mSettings;
    mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    bus.register(this);
  }

  @Subscribe public void handleNotificationData(final NotificationDataAvailable event) {
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
      final PlayState state) {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      return;
    }

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
    mNormalView = new RemoteViews(mContext.getPackageName(), R.layout.ui_notification_control);
    updateNormalNotification(artist, title, cover);

    mBuilder.setContentIntent(RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.OPEN, mContext));
    mNormalView.setOnClickPendingIntent(R.id.notification_play,
        RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, mContext));
    mNormalView.setOnClickPendingIntent(R.id.notification_next,
        RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, mContext));
    mNormalView.setOnClickPendingIntent(R.id.notification_close,
        RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.CLOSE, mContext));

    mNotification = mBuilder.build();

    if (isJellyBean()) {
      mExpandedView = new RemoteViews(mContext.getPackageName(), R.layout.ui_notification_control_expanded);
      updateExpandedNotification(artist, title, album, cover);
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_playpause,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, mContext));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_next,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, mContext));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_previous,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PREVIOUS, mContext));
      mExpandedView.setOnClickPendingIntent(R.id.expanded_notification_remove,
          RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.CLOSE, mContext));
      mNotification.bigContentView = mExpandedView;
    }

    updatePlayState(state);

    mNotification.contentView = mNormalView;
    mNotification.flags = Notification.FLAG_ONGOING_EVENT;
    mNotification.icon = R.drawable.ic_mbrc_status;
    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, mNotification);
  }

  /**
   * Builds a {@link Notification.MediaStyle} style Notification to display.
   * Used only on {@link android.os.Build.VERSION_CODES#LOLLIPOP}.
   *
   * @param event A notification event that the notification service received
   */
  @SuppressLint("NewApi") private void buildLollipopNotification(NotificationDataAvailable event) {
    int playStateIcon = event.getState() == PlayState.Playing ? R.drawable.ic_action_pause : R.drawable.ic_action_play;

    final Notification.MediaStyle mediaStyle = new Notification.MediaStyle();

    mediaStyle.setMediaSession((MediaSession.Token) sessionManager.getMediaSessionToken().getToken());

    Notification.Builder builder = new Notification.Builder(mContext).setVisibility(Notification.VISIBILITY_PUBLIC)
        .setSmallIcon(R.drawable.ic_mbrc_status)
        .addAction(R.drawable.ic_action_previous,
            "Previous",
            RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PREVIOUS, mContext))
        .addAction(playStateIcon,
            "Play/Pause",
            RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.PLAY, mContext))
        .addAction(R.drawable.ic_action_next,
            "Next",
            RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.NEXT, mContext))
        .setStyle(mediaStyle.setShowActionsInCompactView(1, 2))
        .setContentTitle(event.getTitle())
        .setContentText(event.getArtist())
        .setDeleteIntent(RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.CANCEL, mContext))
        .setSubText(event.getAlbum());

    builder.setContentIntent(RemoteViewIntentBuilder.getPendingIntent(RemoteViewIntentBuilder.OPEN, mContext));

    if (event.getCover() != null) {
      builder.setLargeIcon(event.getCover());
    } else {
      Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_image_no_cover);
      builder.setLargeIcon(icon);
    }

    mNotification = builder.build();

    mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, mNotification);
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

  private void updatePlayState(final PlayState state) {
    if (mNormalView == null || mNotification == null) {
      return;
    }

    mNormalView.setImageViewResource(R.id.notification_play,
        state == PlayState.Playing ? R.drawable.ic_action_pause : R.drawable.ic_action_play);

    if (isJellyBean() && mExpandedView != null) {

      mExpandedView.setImageViewResource(R.id.expanded_notification_playpause,
          state == PlayState.Playing ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
    }
  }

  public void cancelNotification(final int notificationId) {
    mNotificationManager.cancel(notificationId);
  }

  @SuppressLint("NewApi") public void updateAvailableNotificationBuilder() {
    NotificationCompat.Builder
        mBuilder
        = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_mbrc_status)
        .setContentTitle(mContext.getString(R.string.application_name))
        .setContentText(mContext.getString(R.string.notification_plugin_out_of_date));

    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    } else {
      resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    resultIntent.setData(Uri.parse("http://kelsos.net/musicbeeremote/download/"));

    PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
        0,
        resultIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    mBuilder.setContentIntent(resultPendingIntent);
    final Notification notification = mBuilder.build();
    notification.flags = Notification.FLAG_AUTO_CANCEL;
    mNotificationManager.notify(PLUGIN_OUT_OF_DATE, notification);
  }
}
