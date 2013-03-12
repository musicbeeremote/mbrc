package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.others.Protocol;

import java.util.LinkedHashMap;

public class UpdatePlayerStatus implements ICommand {
    @Inject
    MainDataModel model;
    @Override
    public void execute(IEvent e) {
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>)e.getData();
        model.setPlayState(map.get(Protocol.PlayerState));
        model.setMuteState(map.get(Protocol.PlayerMute));
        model.setRepeatState(map.get(Protocol.PlayerRepeat));
        model.setShuffleState(Boolean.parseBoolean(map.get(Protocol.PlayerShuffle)));
        model.setVolume(Integer.parseInt(map.get(Protocol.PlayerVolume)));
    }
}
