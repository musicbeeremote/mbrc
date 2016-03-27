package com.kelsos.mbrc.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.ChangeWebSocketStatusEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.messaging.SocketMessageHandler;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.receivers.PlayerActionReceiver;
import com.kelsos.mbrc.services.ServiceDiscovery;
import com.kelsos.mbrc.utilities.LibrarySyncManager;
import com.kelsos.mbrc.receivers.StateBroadcastReceiver;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.utilities.SettingsManager;
import com.raizlabs.android.dbflow.config.FlowManager;
import roboguice.RoboGuice;
import rx.Observable;
import timber.log.Timber;

@Singleton public class Controller extends Service {

  @Inject private SocketService socket;
  @Inject private StateBroadcastReceiver receiver;
  @Inject private PlayerActionReceiver actionReceiver;
  @Inject private NotificationService notificationService;
  @Inject private ServiceDiscovery discovery;
  @Inject private SettingsManager settingsManager;
  @Inject private SocketMessageHandler messageHandler;
  @Inject private LibrarySyncManager syncManager;
  @Inject private RxBus bus;

  public Controller() {
    Timber.d("Application Controller Initialized");
  }

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    FlowManager.init(getApplicationContext());
    RoboGuice.getInjector(this).injectMembers(this);
    this.registerReceiver(actionReceiver, actionReceiver.getIntentFilter());
    this.registerReceiver(receiver, receiver.getIntentFilter());
    bus.register(this, ChangeWebSocketStatusEvent.class, this::onWebSocketActionRequest);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.v("[Service] start command received");
    Observable.merge(discovery.startDiscovery(), settingsManager.getDefault())
        .first()
        .subscribe(connectionSettings -> {
          if (connectionSettings != null) {
            socket.startWebSocket();
          }

    }, t -> Timber.v(t, "Discovery failed"));

    return super.onStartCommand(intent, flags, startId);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Timber.v("[Service] destroying service");
    bus.unregister(this);
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
    socket.disconnect();
    FlowManager.destroy();
    this.unregisterReceiver(receiver);
    this.unregisterReceiver(actionReceiver);
  }

  private void onWebSocketActionRequest(ChangeWebSocketStatusEvent event) {
    switch (event.getAction()) {
      case ChangeWebSocketStatusEvent.CONNECT:
        Timber.v("Attempting to start the websocket");
        socket.startWebSocket();
        break;
      case ChangeWebSocketStatusEvent.DISCONNECT:
        Timber.v("Attempting to stop the websocket");
        socket.disconnect();
        break;
    }
  }


}
