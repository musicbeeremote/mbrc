package com.kelsos.mbrc;

import android.app.Application;
import android.content.Intent;
import android.view.ViewConfiguration;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.data.SyncHandler;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.rest.RemoteClient;
import com.kelsos.mbrc.util.NotificationService;
import com.kelsos.mbrc.util.RemoteBroadcastReceiver;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import java.lang.reflect.Field;

public class RemoteApplication extends Application {

    private static final Logger logger = LoggerManager.getLogger();

    public void onCreate() {
        super.onCreate();
        final Modules.OverriddenModuleBuilder override = Modules.override(RoboGuice.newDefaultRoboModule(this));
        final Module module = override.with(new RemoteModule());
        RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, module);
        final RoboInjector injector = RoboGuice.getInjector(this);

        startService(new Intent(this, Controller.class));

        //Initialization of the background service
        injector.getInstance(Model.class);

        injector.getInstance(SocketService.class);
        injector.getInstance(RemoteBroadcastReceiver.class);
        injector.getInstance(NotificationService.class);
        injector.getInstance(SyncHandler.class);
        injector.getInstance(RemoteClient.class);

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
                logger.i("force overflow hack");
            }
        }
    }

}
