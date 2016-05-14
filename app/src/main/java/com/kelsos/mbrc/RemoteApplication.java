package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.ViewConfiguration;
import com.kelsos.mbrc.controller.RemoteService;
import java.lang.reflect.Field;
import roboguice.RoboGuice;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    RoboGuice.setupBaseApplicationInjector(this);

    startService(new Intent(this, RemoteService.class));

    //HACK: Force overflow code courtesy of Timo Ohr http://stackoverflow.com/a/11438245
    try {
      ViewConfiguration config = ViewConfiguration.get(this);
      Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
      if (menuKeyField != null) {
        menuKeyField.setAccessible(true);
        menuKeyField.setBoolean(config, false);
      }
    } catch (Exception ex) {
      if (BuildConfig.DEBUG) {
        Log.d("mbrc-log", "force overflow hack", ex);
      }
    }
  }
}
