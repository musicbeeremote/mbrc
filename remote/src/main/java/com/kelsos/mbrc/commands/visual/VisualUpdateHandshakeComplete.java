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

        if (!isComplete) return;
        service.sendData(new SocketMessage(Protocol.NowPlayingTrack, Protocol.Request));
        service.sendData(new SocketMessage(Protocol.PlayerStatus, Protocol.Request));
        service.sendData(new SocketMessage(Protocol.NowPlayingCover, Protocol.Request));
        service.sendData(new SocketMessage(Protocol.NowPlayingLyrics, Protocol.Request));
        service.sendData(new SocketMessage(Protocol.NowPlayingPosition, Protocol.Request));
        service.sendData(new SocketMessage(Protocol.PluginVersion, Protocol.Request));
    }
}


