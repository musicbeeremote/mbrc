package kelsos.mbremote;

import android.app.Application;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import kelsos.mbremote.controller.Controller;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Services.ProtocolHandler;
import kelsos.mbremote.Services.SocketService;
import roboguice.RoboGuice;

public class RemoteApplication extends Application
{

	public void onCreate()
	{
		super.onCreate();

		RoboGuice.setBaseApplicationInjector(this, Stage.PRODUCTION, Modules.override(RoboGuice.newDefaultRoboModule(this)).with(new RemoteModule()));

		Injector injector = RoboGuice.getInjector(this);

		//Just getting the instances ready to start working
		Controller controller = injector.getInstance(Controller.class);
		MainDataModel model = injector.getInstance(MainDataModel.class);
		ProtocolHandler protocolHandler = injector.getInstance(ProtocolHandler.class);
		SocketService socketService = injector.getInstance(SocketService.class);
	}
}
