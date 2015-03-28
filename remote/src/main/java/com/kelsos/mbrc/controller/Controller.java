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
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.Map;
import roboguice.service.RoboService;

@Singleton public class Controller extends RoboService {

  private final IBinder mBinder = new ControllerBinder();
  @Inject private Injector injector;
  @Inject private Bus bus;
  private Map<String, Class<? extends ICommand>> commandMap;

  public Controller() {
  }

  @Override public IBinder onBind(Intent intent) {
    return mBinder;
  }

  public void register(String type, Class<? extends ICommand> command) {
    if (commandMap == null) {
      commandMap = new HashMap<>();
    }
    if (!commandMap.containsKey(type)) {
      commandMap.put(type, command);
    }
  }

  public void unregister(String type, Class<? extends ICommand> command) {
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
    final Class<? extends ICommand> commandClass = commandMap.get(event.getType());
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

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    bus.register(this);
    CommandRegistration.register(this);
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    executeCommand(new MessageEvent(UserInputEventType.CancelNotification));
    super.onDestroy();
  }

  public class ControllerBinder extends Binder {
    @SuppressWarnings("unused") ControllerBinder getService() {
      return ControllerBinder.this;
    }
  }
}
