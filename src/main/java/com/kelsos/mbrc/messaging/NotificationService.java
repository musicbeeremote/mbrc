package com.kelsos.mbrc.messaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.ui.activities.MainFragmentActivity;
import com.kelsos.mbrc.ui.activities.UpdateView;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Subscribe;

@Singleton
public class NotificationService {
    public static final int PLUGIN_OUT_OF_DATE = 15612;
    public static final int NOW_PLAYING_PLACEHOLDER = 15613;
    public static final String NOTIFICATION_PLAY_PRESSED = "com.kelsos.mbrc.notification.play";
    public static final String NOTIFICATION_NEXT_PRESSED = "com.kelsos.mbrc.notification.next";
    public static final String NOTIFICATION_CLOSE_PRESSED = "com.kelsos.mbrc.notification.close";
    private Context mContext;
    private MainThreadBusWrapper bus;

    @Inject public NotificationService(Context context, MainThreadBusWrapper bus) {
        this.mContext = context;
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe public void handleNotificationData(NotificationDataAvailable event){
        notificationBuilder(event.getTitle(),event.getArtist(),event.getCover(),event.getState());
    }

    /**
     * Creates an ongoing notification that displays the cover and information about the playing track,
     * and also provides controls to skip or play/pause.
     * @param title The title of the track playing.
     * @param artist The artist of the track playing.
     * @param cover The cover Bitmap.
     * @param state The current play state is used to display the proper play or pause icon.
     */
    private void notificationBuilder(String title, String artist, Bitmap cover, PlayState state) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);

        Intent notificationIntent = new Intent(mContext, MainFragmentActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notificationPendingIntent);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ui_notification_control);
        Intent playPressedIntent = new Intent(NOTIFICATION_PLAY_PRESSED);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(mContext, 1, playPressedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.notification_play, mediaPendingIntent);
        Intent mediaNextButtonIntent = new Intent(NOTIFICATION_NEXT_PRESSED);
        PendingIntent mediaNextButtonPendingIntent = PendingIntent.getBroadcast(mContext, 2, mediaNextButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.notification_next, mediaNextButtonPendingIntent);
        Intent clearNotificationIntent = new Intent(NOTIFICATION_CLOSE_PRESSED);
        PendingIntent clearNotificationPendingIntent = PendingIntent.getBroadcast(mContext, 3, clearNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.notification_close, clearNotificationPendingIntent);

        views.setTextViewText(R.id.notification_artist, artist);
        views.setTextViewText(R.id.notification_title, title);
        if (cover != null) {
            views.setImageViewBitmap(R.id.notification_album_art, cover);
        } else {
            views.setImageViewResource(R.id.notification_album_art, R.drawable.ic_image_no_cover);
        }

        switch (state) {
            case Playing:
                views.setImageViewResource(R.id.notification_play, R.drawable.ic_action_play);
                break;
            case Paused:
                views.setImageViewResource(R.id.notification_play, R.drawable.ic_action_pause);
                break;
            case Stopped:
                views.setImageViewResource(R.id.notification_play, R.drawable.ic_action_play);
                break;
            case Undefined:
                break;
        }

        views.setImageViewResource(R.id.notification_next, R.drawable.ic_action_next);

        views.setImageViewResource(R.id.notification_close, R.drawable.ic_action_collapse);


        Notification notification = mBuilder.build();
        notification.contentView = views;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.drawable.ic_mbrc_status;
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
    }

    public void cancelNotification(int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    public void updateAvailableNotificationBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_mbrc_status)
                .setContentTitle(mContext.getString(R.string.application_name))
                .setContentText(mContext.getString(R.string.notification_plugin_out_of_date));

        Intent resultIntent = new Intent(mContext, UpdateView.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        final Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLUGIN_OUT_OF_DATE, notification);
    }
}