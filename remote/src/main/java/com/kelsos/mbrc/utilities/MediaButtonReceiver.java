package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.Intent;
import roboguice.receiver.RoboBroadcastReceiver;
import roboguice.util.Ln;

public class MediaButtonReceiver extends RoboBroadcastReceiver {

  @Override protected void handleReceive(Context context, Intent intent) {
    super.handleReceive(context, intent);
    Ln.d(intent.getAction());
  }
}
