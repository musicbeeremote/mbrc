package com.kelsos.mbrc.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import javax.inject.Inject;
import toothpick.Scope;
import toothpick.Toothpick;

public class MediaButtonReceiver extends BroadcastReceiver {
  @Inject MediaIntentHandler handler;
  private Scope scope;

  @Override
  public void onReceive(Context context, Intent intent) {
    if (scope == null) {
      scope = Toothpick.openScope(context.getApplicationContext());
      Toothpick.inject(this, scope);
    }

    handler.handleMediaIntent(intent);
  }
}
