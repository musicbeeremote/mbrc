package com.kelsos.mbrc.controller;

import android.app.Application;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import roboguice.RoboGuice;
import roboguice.inject.ContextScope;
import timber.log.Timber;

public class RemoteController implements Runnable {
  private Injector injector;
  private Map<String, Class<? extends ICommand>> commandMap;
  private LinkedBlockingQueue<IEvent> eventQueue;
  private ContextScope scope;
  private final Context context;

  @Inject public RemoteController(Bus bus, Injector injector, Application app) {
    this.injector = injector;
    eventQueue = new LinkedBlockingQueue<>();
    bus.register(this);
    scope = RoboGuice.getInjector(app).getInstance(ContextScope.class);
    context = app.getApplicationContext();
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
    eventQueue.add(event);
  }

  public synchronized void executeCommand(IEvent event) {
    final Class<? extends ICommand> commandClass = commandMap.get(event.getType());
    if (commandClass == null) {
      return;
    }
    ICommand commandInstance;
    try {
      scope.enter(context, new HashMap<>());
      commandInstance = injector.getInstance(commandClass);
      scope.exit(context);
      if (commandInstance == null) {
        return;
      }
      commandInstance.execute(event);
    } catch (Exception ex) {
      Timber.d(ex, "executing command for type: \t%s", event.getType());
      Timber.d("command data: \t%s", event.getData());
    }
  }

  @Override public void run() {
    try {
      //noinspection InfiniteLoopStatement
      while (true) {
        executeCommand(eventQueue.take());
      }
    } catch (InterruptedException e) {
      Timber.d(e, "Failed to execute command");
    }
  }
}
