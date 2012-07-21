package kelsos.mbremote.Controller;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Subscribe;
import kelsos.mbremote.BusAdapter;
import kelsos.mbremote.Command.UpdateMainViewCommand;
import kelsos.mbremote.Enumerations.UserAction;
import kelsos.mbremote.Events.ModelDataEvent;
import kelsos.mbremote.Events.ProtocolDataEvent;
import kelsos.mbremote.Events.RawSocketDataEvent;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Interfaces.IEventType;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Others.SettingsManager;
import kelsos.mbremote.Services.ProtocolHandler;
import kelsos.mbremote.Services.SocketService;
import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.service.RoboService;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Controller extends RoboService
{
	@Inject
	private SocketService socketService;
	@Inject
	private ProtocolHandler protocolHandler;
	@Inject
	private MainDataModel model;
	@Inject
	private SettingsManager settings;
	@Inject
	ConnectivityManager conManager;
	@Inject
	private BusAdapter busAdapter;

	protected Map<IEventType, ICommand> commandMap;

	/**
	 * When the OnCreateEvent gets fired the function catches it and registers the Controller to the event bus singleton
	 * provided by the BusAdapter Class.
	 * @param e
	 */
	public void handleActivityStart(@Observes OnCreateEvent e)
	{
		busAdapter.register(this);
		//TEMP
		registerCommand(UserAction.PlayPause, new UpdateMainViewCommand());
	}

	private final IBinder mBinder = new ControllerBinder();

	public class ControllerBinder extends Binder
	{
		ControllerBinder getService()
		{
			return ControllerBinder.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return mBinder;
	}

	public void registerCommand(IEventType type, ICommand command)
	{
		if (commandMap == null)
		{
			commandMap = new HashMap<IEventType, ICommand>();
		}
		if (!commandMap.containsKey(type))
		{
			commandMap.put(type, command);
		}
	}

	public void unregisterCommand(IEventType type)
	{
		if (commandMap.containsKey(type))
		{
			commandMap.remove(type);
		}
	}

	public void executeCommand(IEvent event)
	{
		ICommand commandInstance = this.commandMap.get(event.getType());
		if (commandInstance != null)
		{
			commandInstance.execute(event);
		}
	}

	/**
	 * @param event
	 */
	@Subscribe
	public void handleSocketDataEvent(ProtocolDataEvent event)
	{
		executeCommand(event);
	}

	@Subscribe
	public void handleUserActionEvents(UserActionEvent event)
	{
		executeCommand(event);
	}

	@Subscribe
	public void handleModelDataEvent(ModelDataEvent event)
	{
		executeCommand(event);
	}

	@Subscribe
	public void handleRawSocketData(RawSocketDataEvent event)
	{
		executeCommand(event);
	}
}
