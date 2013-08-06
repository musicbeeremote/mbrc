package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.SocketMessage;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.services.SocketService;

public class ReduceVolumeOnRingCommand implements ICommand {
    private MainDataModel model;
    private SocketService service;

    @Inject public ReduceVolumeOnRingCommand(MainDataModel model, SocketService service) {
        this.model = model;
        this.service = service;
    }

    @Override public void execute(IEvent e) {
        service.sendData(new SocketMessage(Protocol.PlayerVolume, Protocol.Request, (int) (model.getVolume() * 0.2)));
    }
}
