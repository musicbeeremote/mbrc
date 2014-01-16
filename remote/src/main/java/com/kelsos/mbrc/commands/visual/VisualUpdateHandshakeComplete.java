package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.net.SocketService;

public class VisualUpdateHandshakeComplete implements ICommand {
    private SocketService service;
    private MainDataModel model;

    @Inject public VisualUpdateHandshakeComplete(SocketService service, MainDataModel model) {
        this.service = service;
        this.model = model;
    }

    public void execute(IEvent e) {
        boolean isComplete = (Boolean) e.getData();
        model.setHandShakeDone(isComplete);

        if (!isComplete) {
            return;
        }

        service.sendData(new SocketMessage(Protocol.NOW_PLAYING_TRACK, Protocol.REQUEST));
        service.sendData(new SocketMessage(Protocol.PLAYER_STATUS, Protocol.REQUEST));
        service.sendData(new SocketMessage(Protocol.NOW_PLAYING_COVER, Protocol.REQUEST));
        service.sendData(new SocketMessage(Protocol.NOW_PLAYING_LYRICS, Protocol.REQUEST));
        service.sendData(new SocketMessage(Protocol.NOW_PLAYING_POSITION, Protocol.REQUEST));
        service.sendData(new SocketMessage(Protocol.PLUGIN_VERSION, Protocol.REQUEST));
    }
}


