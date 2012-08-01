package kelsos.mbremote.Controller;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import kelsos.mbremote.Events.ModelDataEvent;
import kelsos.mbremote.Events.ProtocolDataEvent;
import kelsos.mbremote.Events.RawSocketDataEvent;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.Interfaces.ICommand;
import kelsos.mbremote.Interfaces.IEvent;
import kelsos.mbremote.Interfaces.IEventType;
import kelsos.mbremote.configuration.MainViewCommandRegistration;
import kelsos.mbremote.configuration.ProtocolHandlerCommandRegistration;
import kelsos.mbremote.configuration.SocketServiceCommandRegistration;
import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.service.RoboService;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Controller extends RoboService
{
	private Injector injector;
	private Bus bus;
	private Map<IEventType, Class<?>> commandMap;

	@Inject
	public Controller(Bus bus, Injector injector)
	{
		this.bus = bus;
		this.injector = injector;
		MainViewCommandRegistration.registerCommands(this);
		ProtocolHandlerCommandRegistration.register(this);
		SocketServiceCommandRegistration.register(this);
	}

	/**
	 * When the OnCreateEvent gets fired the function catches it and registers the Controller to the event bus singleton
	 * provided by the Otto Event bus.
	 * @param e
	 */
	public void handleActivityStart(@Observes OnCreateEvent e)
	{
		bus.register(this);
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

	public void registerCommand(IEventType type, Class<?> command)
	{
		if (commandMap == null)
		{
			commandMap = new HashMap<IEventType, Class<?>>();
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
		Class<ICommand> commandClass =(Class<ICommand>)this.commandMap.get(event.getType());
		if(commandClass == null) return;
		ICommand commandInstance = null;
		try{
			commandInstance = commandClass.newInstance();
			injector.injectMembers(commandInstance);
			if(commandInstance==null) return;
			commandInstance.execute(event);

		}
		catch (Exception ex)
		{

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
