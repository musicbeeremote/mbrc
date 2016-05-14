package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;

public class CancelNotificationCommand implements ICommand {
  private NotificationService notificationService;

  @Inject public CancelNotificationCommand(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Override public void execute(IEvent e) {
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
  }
}
