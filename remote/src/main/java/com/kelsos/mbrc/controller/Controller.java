package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.data.model.Model;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.net.ServiceDiscovery;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.util.Logger;
import com.kelsos.mbrc.util.NotificationService;
import com.kelsos.mbrc.util.RemoteBroadcastReceiver;
import com.kelsos.mbrc.util.SettingsManager;
import roboguice.service.RoboService;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

@Singleton
public class Controller extends RoboService {

    @Inject
    private Model model;
    @Inject
    private SocketService socket;
    @Inject
    private RemoteBroadcastReceiver receiver;
    @Inject
    private NotificationService notificationService;
    @Inject
    private ServiceDiscovery discovery;
    @Inject
    private SettingsManager settingsManager;
    @Inject
    private SyncManager syncManager;

    public Controller() {
        Ln.d("Application Controller Initialized");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Events.Messages.onNext(new Message(UserInputEventType.START_CONNECTION));
        discovery.startDiscovery();
        init();

		Events.Messages.subscribeOn(Schedulers.io())
				.filter(msg -> msg.getType().equals(UserInputEventType.START_DISCOVERY))
				.subscribe(msg -> discovery.startDiscovery(), Logger::LogThrowable);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    public void init() {
        syncManager.clearCurrentQueue();
        syncManager.startCurrentQueueSyncing();
    }
}
