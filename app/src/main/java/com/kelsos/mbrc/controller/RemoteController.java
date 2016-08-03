package com.kelsos.mbrc.controller;

import android.app.Application;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import javax.inject.Inject;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

public class RemoteController implements Runnable {
  private final Scope scope;
  private Map<String, Class<? extends ICommand>> commandMap;
  private LinkedBlockingQueue<IEvent> eventQueue;

  @Inject
  public RemoteController(RxBus bus, Application app) {
    eventQueue = new LinkedBlockingQueue<>();
    bus.register(this, MessageEvent.class, this::handleUserActionEvents);
    scope = Toothpick.openScope(app);
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

  void handleUserActionEvents(MessageEvent event) {
    eventQueue.add(event);
  }

  synchronized void executeCommand(IEvent event) {
    final Class<? extends ICommand> commandClass = commandMap.get(event.getType());
    if (commandClass == null) {
      return;
    }
    ICommand commandInstance;
    try {

      commandInstance = scope.getInstance(commandClass);
      if (commandInstance == null) {
        return;
      }
      commandInstance.execute(event);
    } catch (Exception ex) {
      Timber.d(ex, "executing command for type: \t%s", event.getType());
      Timber.d("command data: \t%s", event.getData());
    }
  }

  @Override
  public void run() {
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
