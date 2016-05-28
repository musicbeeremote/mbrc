package com.kelsos.mbrc.commands;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.services.SocketService;

public class ConnectionStatusChangedCommand implements ICommand {
  private MainDataModel model;
  private SocketService service;
  private NotificationService notificationService;
  private Context context;

  @Inject public ConnectionStatusChangedCommand(MainDataModel model, SocketService service,
      NotificationService notificationService, Context context) {
    this.model = model;
    this.service = service;
    this.notificationService = notificationService;
    this.context = context;
  }

  public void execute(IEvent e) {
    model.setConnectionState(e.getDataString());
    if (model.isConnectionActive()) {
      service.sendData(SocketMessage.create(Protocol.Player, "Android"));
      context.sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE));
    } else {
      notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
    }
  }
}
