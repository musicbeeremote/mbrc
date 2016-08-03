package com.kelsos.mbrc.di.providers;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import javax.inject.Inject;
import javax.inject.Provider;

public class NotificationManagerCompatProvider implements Provider<NotificationManagerCompat> {
  private Context context;

  @Inject
  public NotificationManagerCompatProvider(Context context) {
    this.context = context;
  }

  @Override
  public NotificationManagerCompat get() {
    return NotificationManagerCompat.from(context);
  }
}
