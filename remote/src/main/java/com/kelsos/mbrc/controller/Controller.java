package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.data.model.PlayerModel;
import com.kelsos.mbrc.data.model.TrackModel;
import com.kelsos.mbrc.enums.SocketAction;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.services.ServiceDiscovery;
import com.kelsos.mbrc.utilities.RemoteBroadcastReceiver;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import roboguice.service.RoboService;
import roboguice.util.Ln;

@Singleton public class Controller extends RoboService {

  @Inject private SocketService socket;
  @Inject private RemoteBroadcastReceiver receiver;
  @Inject private NotificationService notificationService;
  @Inject private ServiceDiscovery discovery;
  @Inject private SettingsManager settingsManager;
  @Inject private SyncManager syncManager;
  @Inject private TrackModel trackModel;
  @Inject private PlayerModel playerModel;
  @Inject private RemoteApi api;

  public Controller() {
    Ln.d("Application Controller Initialized");
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    discovery.startDiscovery();

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
