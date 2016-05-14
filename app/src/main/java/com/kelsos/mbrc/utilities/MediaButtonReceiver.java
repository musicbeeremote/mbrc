package com.kelsos.mbrc.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import roboguice.RoboGuice;

public class MediaButtonReceiver extends BroadcastReceiver {
  @Inject private MediaIntentHandler handler;

  @Override public void onReceive(Context context, Intent intent) {
    if (handler == null) {
      RoboGuice.getInjector(context).injectMembers(this);
    }

    handler.handleMediaIntent(intent);
  }
}
