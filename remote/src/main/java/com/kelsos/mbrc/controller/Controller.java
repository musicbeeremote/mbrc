package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.data.model.PlayerState;
import com.kelsos.mbrc.data.model.TrackState;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.net.ServiceDiscovery;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.Logger;
import com.kelsos.mbrc.util.NotificationService;
import com.kelsos.mbrc.util.RemoteBroadcastReceiver;
import com.kelsos.mbrc.util.SettingsManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import roboguice.service.RoboService;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

@Singleton public class Controller extends RoboService {

  @Inject private SocketService socket;
  @Inject private RemoteBroadcastReceiver receiver;
  @Inject private NotificationService notificationService;
  @Inject private ServiceDiscovery discovery;
  @Inject private SettingsManager settingsManager;
  @Inject private SyncManager syncManager;
  @Inject private TrackState trackState;
  @Inject private PlayerState playerState;
  @Inject private RemoteApi api;

  public Controller() {
    Ln.d("Application Controller Initialized");
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Events.messages.onNext(new Message(EventType.START_CONNECTION));
    discovery.startDiscovery();

    Events.messages.subscribeOn(Schedulers.io())
        .filter(msg -> EventType.START_DISCOVERY.equals(msg.getType()))
        .subscribe(msg -> discovery.startDiscovery(), Logger::logThrowable);

    //DatabaseUtils.createDatabaseTrigger(daoSession.getDatabase());
    FlowManager.init(getApplicationContext());

    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    socket.socketManager(SocketAction.STOP);
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
    FlowManager.destroy();
    this.unregisterReceiver(receiver);
  }
}
