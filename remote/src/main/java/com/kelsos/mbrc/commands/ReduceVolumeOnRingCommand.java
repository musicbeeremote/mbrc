package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.net.SocketService;

public class ReduceVolumeOnRingCommand implements ICommand {
    public static final double TWENTY_PERCENT = 0.2;
    private MainDataModel model;
    private SocketService service;

    @Inject public ReduceVolumeOnRingCommand(MainDataModel model, SocketService service) {
        this.model = model;
        this.service = service;
    }

    @Override public void execute(IEvent e) {
        service.sendData(new SocketMessage(Protocol.PlayerVolume, Protocol.Request, (int) (model.getVolume() * TWENTY_PERCENT)));
    }
}
