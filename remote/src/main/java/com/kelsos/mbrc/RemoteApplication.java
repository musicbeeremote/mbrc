package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import android.support.multidex.MultiDex;
import com.kelsos.mbrc.controller.Controller;
import roboguice.RoboGuice;
import timber.log.Timber;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    MultiDex.install(this);
    RoboGuice.setupBaseApplicationInjector(this);
    startService(new Intent(this, Controller.class));

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }
}
