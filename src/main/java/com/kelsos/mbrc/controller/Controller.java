package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.configuration.ProtocolHandlerCommandRegistration;
import com.kelsos.mbrc.configuration.SocketServiceCommandRegistration;
import com.kelsos.mbrc.events.ModelDataEvent;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.events.RawSocketDataEvent;
import com.kelsos.mbrc.events.UserActionEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.interfaces.IEventType;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
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
		bus.register(this);
		ProtocolHandlerCommandRegistration.register(this);
		SocketServiceCommandRegistration.register(this);
	}

	public Controller(){};

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

	public void unRegisterCommand(IEventType type)
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
			commandInstance=injector.getInstance(commandClass);
			if(commandInstance==null) return;
			commandInstance.execute(event);
		}
		catch (Exception ex)
		{
			//Log.d("Controller","Command Execute", ex);
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
