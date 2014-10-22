package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.SocketService;
import com.kelsos.mbrc.rest.RemoteApi;

public class ReduceVolumeOnRingCommand implements ICommand {
    public static final double TWENTY_PERCENT = 0.2;

    @Inject
    private RemoteApi api;
    private Model model;
    private SocketService service;

    @Inject public ReduceVolumeOnRingCommand(Model model, SocketService service) {
        this.model = model;
        this.service = service;
    }

    @Override public void execute(IEvent e) {
        int volume = (int) (model.getVolume() * TWENTY_PERCENT);
        api.updateVolume(volume);
    }
}
