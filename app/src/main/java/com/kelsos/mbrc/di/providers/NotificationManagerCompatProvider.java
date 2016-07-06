package com.kelsos.mbrc.di.providers;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class NotificationManagerCompatProvider implements Provider<NotificationManagerCompat> {
  @Inject
  private Context context;

  @Override
  public NotificationManagerCompat get() {
    return NotificationManagerCompat.from(context);
  }
}
