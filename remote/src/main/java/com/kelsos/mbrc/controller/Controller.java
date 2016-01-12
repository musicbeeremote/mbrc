package com.kelsos.mbrc.controller;

import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.messaging.SocketMessageHandler;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.services.ServiceDiscovery;
import com.kelsos.mbrc.utilities.LibrarySyncManager;
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
  @Inject private SocketMessageHandler handler;
  @Inject private LibrarySyncManager syncManager;

  public Controller() {
    Ln.d("Application Controller Initialized");
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    discovery.startDiscovery().subscribe(connectionSettings -> {

    }, Ln::v);
    FlowManager.init(getApplicationContext());
    socket.startWebSocket();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
    FlowManager.destroy();
    this.unregisterReceiver(receiver);
  }
}
