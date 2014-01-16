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
    public static final int LOWEST_NOT_ZERO = 10;
    public static final int STEP = 10;
    public static final int MOD = 10;
    public static final int BASE = 0;
    public static final int LIMIT = 5;
    private MainDataModel model;
    private Bus bus;

    @Inject public KeyVolumeDownCommand(MainDataModel model, Bus bus) {
        this.model = model;
        this.bus = bus;
    }

    @Override public void execute(IEvent e) {
        if (model.getVolume() >= LOWEST_NOT_ZERO) {
            int mod = model.getVolume() % MOD;
            int volume;

            if (mod == BASE) {
                volume = model.getVolume() - STEP;
            } else if (mod < LIMIT) {
                volume = model.getVolume() - (STEP + mod);
            } else {
                volume = model.getVolume() - mod;
            }
            bus.post(new MessageEvent(ProtocolEventType.USER_ACTION, new UserAction(Protocol.PLAYER_VOLUME, volume)));
        }
    }
}
