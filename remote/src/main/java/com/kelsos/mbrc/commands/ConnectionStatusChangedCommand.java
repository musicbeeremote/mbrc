package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.util.NotificationService;

public class ConnectionStatusChangedCommand implements ICommand {
    private Model model;
    private NotificationService notificationService;

    @Inject public ConnectionStatusChangedCommand(Model model, NotificationService notificationService) {
        this.model = model;
        this.notificationService = notificationService;
    }

    public void execute(IEvent e) {
        model.setConnectionState(e.getDataString());
        if (!model.getIsConnectionActive()) {
            notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER);
        }
    }
}
