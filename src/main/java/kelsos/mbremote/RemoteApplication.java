package kelsos.mbremote;

import android.app.Application;
import android.os.Build;
import android.view.ViewConfiguration;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Services.ProtocolHandler;
import kelsos.mbremote.Services.SocketService;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.utilities.RemoteBroadcastReceiver;
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
			// Ignore
		}


	}

}
