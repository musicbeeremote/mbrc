package com.kelsos.mbrc.controller;

import android.app.Notification;

public interface ForegroundHooks {
  void start(int id, Notification notification);

  void stop();
}
