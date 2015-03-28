package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.messaging.NotificationService;

public class NotifyPluginOutOfDateCommand implements ICommand {
  private NotificationService notificationService;

  @Inject public NotifyPluginOutOfDateCommand(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @Override public void execute(IEvent e) {
    notificationService.updateAvailableNotificationBuilder();
  }
}
