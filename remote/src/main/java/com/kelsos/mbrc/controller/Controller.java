package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.service.RoboService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Singleton
public class Controller extends RoboService {

    @Inject private Injector injector;
    @Inject private Bus bus;
    private Map<String, Class<? extends ICommand>> commandMap;
    private final BlockingQueue<Runnable> mExecutionQueue;
    private final ThreadPoolExecutor mCommandExecutionThreadPool;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    public Controller() {
        mExecutionQueue = new LinkedBlockingQueue<Runnable>();
        mCommandExecutionThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mExecutionQueue);

        if (BuildConfig.DEBUG) {
            Log.d("mbrc-log", "Controller initialized");
        }
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    public void register(String type, Class<? extends ICommand> command) {
        if (commandMap == null) {
            commandMap = new HashMap<String, Class<? extends ICommand>>();
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
     * Subscriber - Handler to the MessageEvents posted through the event bus.
     * Responsible for passing the event to the executeCommand function.
     * @param event
     */
    @Subscribe public void handleUserActionEvents(MessageEvent event) {
        executeCommand(event);
    }

    public void executeCommand(final IEvent event) {
        Class<? extends ICommand> commandClass = this.commandMap.get(event.getType());
        if (commandClass == null) return;
        try {
            final ICommand commandInstance = injector.getInstance(commandClass);
            if (commandInstance == null) return;
            mCommandExecutionThreadPool.execute(new Runnable() {
                @Override public void run() {
                    commandInstance.execute(event);
                }
            });
        } catch (Exception ex) {
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "executing command for type: \t" + event.getType(), ex);
                Log.d("mbrc-log", "command data: \t" + event.getData());
            }
        }

    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Configuration.initialize(this);
        bus.register(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override public void onDestroy() {
        executeCommand(new MessageEvent(UserInputEventType.CancelNotification));
        super.onDestroy();
    }
}
