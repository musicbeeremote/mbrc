package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import com.kelsos.mbrc.controller.Controller;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    startService(new Intent(this, Controller.class));
  }
}
