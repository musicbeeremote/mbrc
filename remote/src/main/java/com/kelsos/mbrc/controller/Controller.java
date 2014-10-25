package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import roboguice.service.RoboService;
import roboguice.util.Ln;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Controller extends RoboService {
    @Inject private Injector injector;

    private Map<String, Class<? extends ICommand>> commandMap;

    public Controller() {
        if (BuildConfig.DEBUG) {
            Ln.d( "Controller initialized");
        }
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Registers an association between an event type (String) and a command
     * @param type The event type associated.
     * @param command The command associated with the event type.
     */
    public void register(String type, Class<? extends ICommand> command) {
        if (commandMap == null) {
            commandMap = new HashMap<>();
        }
        if (!commandMap.containsKey(type)) {
            commandMap.put(type, command);
        }
    }

    /**
     * Removes an association between an event type (String) and a command
     * @param type The event The event type associated.
     * @param command The command associated with the event type.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void unregister(String type, Class<? extends ICommand> command) {
        if (commandMap.containsKey(type) && commandMap.get(type).equals(command)) {
            commandMap.remove(type);
        }
    }

    /**
     * Checks for a Command associated with the event passed.
     * Instantiates the Command and executes it.
     * @param event the event passed for execution
     */
    private void executeCommand(final IEvent event) {
        Class<? extends ICommand> commandClass = this.commandMap.get(event.getType());
        if (commandClass == null) {
            return;
        }
        try {
            final ICommand commandInstance = injector.getInstance(commandClass);
            if (commandInstance == null) {
                return;
            }

            commandInstance.execute(event);

        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Ln.d("executing command for type: \t" + event.getType(), ex);
                Ln.d( "command data: \t" + event.getData());
            }
        }

    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Configuration.initialize(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onDestroy() {
        executeCommand(new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION));
        super.onDestroy();
    }
}
