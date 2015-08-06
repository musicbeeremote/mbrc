package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import roboguice.receiver.RoboBroadcastReceiver;

public class MediaButtonReceiver extends RoboBroadcastReceiver {
  @Inject MediaIntentHandler handler;

  @Override protected void handleReceive(Context context, Intent intent) {
    handler.handleMediaIntent(intent);
  }
}
