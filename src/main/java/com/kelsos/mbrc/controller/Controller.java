package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.configuration.CommandRegistration;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.UserInputEvent;
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
    @Inject private Injector injector;
    @Inject private Bus bus;
    private Map<String, Class<?>> commandMap;

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
     * Takes a MessageEvent and passes it to the command execution function.
     *
     * @param event The message received.
     */
    @Subscribe public void handleUserActionEvents(MessageEvent event) {
        executeCommand(event);
    }

    public void executeCommand(IEvent event) {
        Class<ICommand> commandClass = (Class<ICommand>) this.commandMap.get(event.getType());
        if (commandClass == null) return;
        ICommand commandInstance;
        try {
            commandInstance = injector.getInstance(commandClass);
            if (commandInstance == null) return;
            commandInstance.execute(event);
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "executing command for type: \t" + event.getType(), ex);
                Log.d("mbrc-log", "command data: \t" + event.getData());
            }
        }

    }

    public class ControllerBinder extends Binder {
        ControllerBinder getService() {
            return ControllerBinder.this;
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        bus.register(this);
        CommandRegistration.register(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override public void onDestroy() {
        executeCommand(new MessageEvent(UserInputEvent.CancelNotification));
        super.onDestroy();
    }
}
