package com.kelsos.mbrc;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.view.ViewConfiguration;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.SocketService;
import com.kelsos.mbrc.controller.Controller;
import com.kelsos.mbrc.utilities.RemoteBroadcastReceiver;
import roboguice.RoboGuice;
import roboguice.inject.RoboInjector;

import java.lang.reflect.Field;

public class RemoteApplication extends Application
{

	public void onCreate()
	{
		super.onCreate();

		RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, Modules.override(RoboGuice.newDefaultRoboModule(this)).with(new RemoteModule()));

		final RoboInjector injector = RoboGuice.getInjector(this);

		//Just getting the instances ready to start working
		Controller controller = injector.getInstance(Controller.class);
		MainDataModel model = injector.getInstance(MainDataModel.class);
		ProtocolHandler protocolHandler = injector.getInstance(ProtocolHandler.class);
		SocketService socketService = injector.getInstance(SocketService.class);
		RemoteBroadcastReceiver remoteBroadcastReceiver = injector.getInstance(RemoteBroadcastReceiver.class);

		if(Build.VERSION.SDK_INT<14) return;
		//HACK: Force overflow code courtesy of Timo Ohr http://stackoverflow.com/a/11438245
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			Log.d("App", "configHack", ex);
		}


	}

}
