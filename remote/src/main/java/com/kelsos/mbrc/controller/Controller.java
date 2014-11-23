package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.model.Model;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.ServiceDiscovery;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.util.NotificationService;
import com.kelsos.mbrc.util.RemoteBroadcastReceiver;
import com.kelsos.mbrc.util.SettingsManager;
import roboguice.service.RoboService;
import roboguice.util.Ln;

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
    private ActionController actionController;


    public Controller() {
        Ln.d("Application Controller Initialized");
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        Events.Messages.onNext(new MessageEvent(UserInputEventType.START_CONNECTION));
        discovery.startDiscovery();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
}
