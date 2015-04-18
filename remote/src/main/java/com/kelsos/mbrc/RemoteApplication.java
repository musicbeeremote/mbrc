package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.ViewConfiguration;
import com.crashlytics.android.Crashlytics;
import com.kelsos.mbrc.controller.RemoteService;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;
import com.kelsos.mbrc.utilities.RemoteBroadcastReceiver;
import io.fabric.sdk.android.Fabric;
import java.lang.reflect.Field;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

public class RemoteApplication extends Application {

  public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics());
    final RoboInjector injector = RoboGuice.getInjector(this);

    startService(new Intent(this, RemoteService.class));

    //Initialization of the background service
    injector.getInstance(MainDataModel.class);
    injector.getInstance(ProtocolHandler.class);
    injector.getInstance(SocketService.class);
    injector.getInstance(RemoteBroadcastReceiver.class);
    injector.getInstance(NotificationService.class);

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
