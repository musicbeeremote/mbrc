package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.configuration.ProtocolHandlerCommandRegistration;
import com.kelsos.mbrc.configuration.SocketServiceCommandRegistration;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.service.RoboService;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Controller extends RoboService {
    private final IBinder mBinder = new ControllerBinder();
    private Injector injector;
    private Map<String, Class<?>> commandMap;

    @Inject public Controller(Bus bus, Injector injector) {
        this.injector = injector;
        bus.register(this);
        ProtocolHandlerCommandRegistration.register(this);
        SocketServiceCommandRegistration.register(this);
    }

    public Controller() {
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void register(String type, Class<?> command) {
        if (commandMap == null) {
            commandMap = new HashMap<String, Class<?>>();
        }
        if (!commandMap.containsKey(type)) {
            commandMap.put(type, command);
        }
    }

    public void unregister(String type, Class<?> command) {
        if (commandMap.containsKey(type) && commandMap.get(type).equals(command)) {
            commandMap.remove(type);
        }
    }

    /**
     * @param event
     */
    @Subscribe public void handleUserActionEvents(MessageEvent event) {
        executeCommand(event);
    }

    public void executeCommand(IEvent event) {
        Class<ICommand> commandClass = (Class<ICommand>) this.commandMap.get(event.getType());
        if (commandClass == null) return;
        ICommand commandInstance = null;
        try {
            commandInstance = injector.getInstance(commandClass);
            if (commandInstance == null) return;
            commandInstance.execute(event);
        } catch (Exception ex) {
            Log.d("Controller", "CommandExecution on: \t" + event.getType().toString(), ex);
            Log.d("Controller", "CommandExecution data: \t" + event.getData());
        }

    }

    public class ControllerBinder extends Binder {
        ControllerBinder getService() {
            return ControllerBinder.this;
        }
    }
}
