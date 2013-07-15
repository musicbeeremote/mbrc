package com.kelsos.mbrc.messaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.kelsos.mbrc.ui.activities.MainFragmentActivity;
import com.kelsos.mbrc.ui.activities.UpdateView;
import com.squareup.otto.Subscribe;

@Singleton
public class NotificationService {
    public static final int PLUGIN_OUT_OF_DATE = 15612;
    public static final int NOW_PLAYING_PLACEHOLDER = 15613;
    public static final String NOTIFICATION_PLAY_PRESSED = "com.kelsos.mbrc.notification.play";
    public static final String NOTIFICATION_NEXT_PRESSED = "com.kelsos.mbrc.notification.next";
    public static final String NOTIFICATION_CLOSE_PRESSED = "com.kelsos.mbrc.notification.close";
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Context context;
    private MainThreadBusWrapper bus;

    @Inject public NotificationService(Context context, MainThreadBusWrapper bus) {
        this.context = context;
        this.bus = bus;
        bus.register(this);
    }

    /**
     * Using an id of the string stored in the strings XML this function
     * displays a toast window.
     */
    public void showToastMessage(final int id) {
        String data = context.getString(id);
        showToast(data);
    }

    /**
     * Given a message
     *
     * @param message the string of a message that will be shown as a toast message.
     */
    private void showToast(final String message) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } else {
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception ex) {

        }
    }

    /**
     * Given a message, it displays the message on a toast window.
     * If the AppNotification manager is not properly initialized
     * nothing happens.
     *
     * @param message the string message that will be shown as a toast message.
     */
    public void showToastMessage(final String message) {
        showToast(message);
    }

    @Subscribe public void handleNotificationData(NotificationDataAvailable event){
        notificationBuilder(event.getTitle(),event.getArtist(),event.getCover(),event.getState());
    }

    private void notificationBuilder(String title, String artist, Bitmap cover, PlayState state) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        Intent notificationIntent = new Intent(context, MainFragmentActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notificationPendingIntent);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ui_notification_control);
        Intent playPressedIntent = new Intent(NOTIFICATION_PLAY_PRESSED);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 1, playPressedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.notification_play, mediaPendingIntent);
        Intent mediaNextButtonIntent = new Intent(NOTIFICATION_NEXT_PRESSED);
        PendingIntent mediaNextButtonPendingIntent = PendingIntent.getBroadcast(context, 2, mediaNextButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.notification_next, mediaNextButtonPendingIntent);
        Intent clearNotificationIntent = new Intent(NOTIFICATION_CLOSE_PRESSED);
        PendingIntent clearNotificationPendingIntent = PendingIntent.getBroadcast(context, 3, clearNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOW_PLAYING_PLACEHOLDER, notification);
    }

    public void cancelNotification(int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    public void updateAvailableNotificationBuilder() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_mbrc_status)
                .setContentTitle(context.getString(R.string.application_name))
                .setContentText(context.getString(R.string.notification_plugin_out_of_date));

        Intent resultIntent = new Intent(context, UpdateView.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        final Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(PLUGIN_OUT_OF_DATE, notification);
    }
}