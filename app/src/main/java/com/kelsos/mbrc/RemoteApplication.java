package com.kelsos.mbrc;

import android.app.Application;
import android.view.ViewConfiguration;
import java.lang.reflect.Field;
import roboguice.RoboGuice;
import timber.log.Timber;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    RoboGuice.setupBaseApplicationInjector(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree() {
        @Override protected String createStackElementTag(StackTraceElement element) {
          return super.createStackElementTag(element) + ":" +
              element.getLineNumber() +
              " [" + Thread.currentThread().getName() + "]";
        }
      });
    }

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
        Timber.e(ex, "force overflow hack");
      }
    }
  }
}
