package com.kelsos.mbrc.controller;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.kelsos.mbrc.configuration.CommandRegistration;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.BrowseSync;
import com.kelsos.mbrc.services.ProtocolHandler;
import com.kelsos.mbrc.services.ServiceDiscovery;
import com.kelsos.mbrc.services.SocketService;
import com.kelsos.mbrc.utilities.RemoteBroadcastReceiver;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

@Singleton
public class RemoteService extends Service implements ForegroundHooks {

  private final IBinder mBinder = new ControllerBinder();
  @Inject RemoteController remoteController;
  @Inject MainDataModel mainDataModel;
  @Inject ProtocolHandler protocolHandler;
  @Inject SocketService socketService;
  @Inject ServiceDiscovery discovery;
  @Inject RemoteBroadcastReceiver receiver;
  @Inject NotificationService notificationService;
  @Inject BrowseSync browseSync;

  private ExecutorService threadPoolExecutor;

  public RemoteService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    Scope scope = Toothpick.openScope(getApplication());
    super.onCreate();
    Toothpick.inject(this, scope);
    FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());
    this.registerReceiver(receiver, receiver.filter());
  }

  /**
   * Takes a MessageEvent and passes it to the command execution function.
   *
   * @param event The message received.
   */
  public void handleUserActionEvents(MessageEvent event) {
    remoteController.handleUserActionEvents(event);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("Background Service::Started");
    notificationService.setForegroundHooks(this);
    CommandRegistration.register(remoteController);
    threadPoolExecutor = Executors.newSingleThreadExecutor();
    threadPoolExecutor.execute(remoteController);
    remoteController.executeCommand(new MessageEvent(UserInputEventType.StartConnection));
    discovery.startDiscovery(() -> browseSync.sync());

    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    remoteController.executeCommand(new MessageEvent(UserInputEventType.CancelNotification));
    remoteController.executeCommand(new MessageEvent(UserInputEventType.TerminateConnection));
    CommandRegistration.unregister(remoteController);
    if (threadPoolExecutor != null) {
      threadPoolExecutor.shutdownNow();
    }
    Timber.d("Background Service::Destroyed");
    this.unregisterReceiver(receiver);
    Toothpick.closeScope(this);
    super.onDestroy();
  }

  @Override
  public void start(int notificationId, Notification notification) {
    Timber.v("Notification is starting foreground");
    startForeground(notificationId, notification);
  }

  @Override
  public void stop() {
    Timber.v("Notification is stopping foreground");
    stopForeground(true);
  }

  private class ControllerBinder extends Binder {
    @SuppressWarnings("unused")
    ControllerBinder getService() {
      return ControllerBinder.this;
    }
  }
}
