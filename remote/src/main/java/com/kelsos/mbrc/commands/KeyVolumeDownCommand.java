package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import com.squareup.otto.Bus;

public class KeyVolumeDownCommand implements ICommand {
    private MainDataModel model;
    private Bus bus;

    @Inject public KeyVolumeDownCommand(MainDataModel model, Bus bus) {
        this.model = model;
        this.bus = bus;
    }

    @Override public void execute(IEvent e) {
        if (model.getVolume() >= 10) {
            int mod = model.getVolume() % 10;
            int volume;

            if (mod == 0) {
                volume = model.getVolume() - 10;
            } else if (mod < 5) {
                volume = model.getVolume() - (10 + mod);
            } else {
                volume = model.getVolume() - mod;
            }
            bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PLAYER_VOLUME, volume)));
        }
    }
}
