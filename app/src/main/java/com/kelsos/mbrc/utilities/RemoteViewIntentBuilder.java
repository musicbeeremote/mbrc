package com.kelsos.mbrc.utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import com.kelsos.mbrc.ui.activities.nav.MainActivity;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RemoteViewIntentBuilder {
  public static final String REMOTE_PLAY_PRESSED = "com.kelsos.mbrc.notification.play";
  public static final String REMOTE_NEXT_PRESSED = "com.kelsos.mbrc.notification.next";
  public static final String REMOTE_CLOSE_PRESSED = "com.kelsos.mbrc.notification.close";
  public static final String REMOTE_PREVIOUS_PRESSED = "com.kelsos.mbrc.notification.previous";
  public static final String CANCELLED_NOTIFICATION = "com.kelsos.mbrc.notification.cancel";
  public static final int OPEN = 0;
  public static final int PLAY = 1;
  public static final int NEXT = 2;
  public static final int CLOSE = 3;
  public static final int PREVIOUS = 4;
  public static final int CANCEL = 5;

  public static PendingIntent getPendingIntent(@ButtonAction int id, Context mContext) {
    switch (id) {
      case OPEN:
        Intent notificationIntent = new Intent(mContext, MainActivity.class);
        return PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      case PLAY:
        Intent playPressedIntent = new Intent(REMOTE_PLAY_PRESSED);
        return PendingIntent.getBroadcast(mContext, 1, playPressedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      case NEXT:
        Intent mediaNextButtonIntent = new Intent(REMOTE_NEXT_PRESSED);
        return PendingIntent.getBroadcast(mContext, 2, mediaNextButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      case CLOSE:
        Intent clearNotificationIntent = new Intent(REMOTE_CLOSE_PRESSED);
        return PendingIntent.getBroadcast(mContext, 3, clearNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      case PREVIOUS:
        Intent mediaPreviousButtonIntent = new Intent(REMOTE_PREVIOUS_PRESSED);
        return PendingIntent.getBroadcast(mContext, 4, mediaPreviousButtonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      case CANCEL:
        Intent cancelIntent = new Intent(CANCELLED_NOTIFICATION);
        return PendingIntent.getBroadcast(mContext, 4, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  @IntDef({
      OPEN,
      PLAY,
      CLOSE,
      PREVIOUS,
      NEXT,
      CANCEL
  }) @Retention(RetentionPolicy.SOURCE) public @interface ButtonAction {
  }
}
