package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.others.Protocol;
import org.codehaus.jackson.node.ObjectNode;

public class UpdatePlayerStatus implements ICommand {
    @Inject MainDataModel model;

    @Override public void execute(IEvent e) {
        ObjectNode node = (ObjectNode)e.getData();
        model.setPlayState(node.path(Protocol.PlayerState).asText());
        model.setMuteState(node.path(Protocol.PlayerMute).asBoolean());
        model.setRepeatState(node.path(Protocol.PlayerRepeat).asText());
        model.setShuffleState(node.path(Protocol.PlayerShuffle).asBoolean());
        model.setScrobbleState(node.path(Protocol.PlayerScrobble).asBoolean());
        model.setVolume(Integer.parseInt(node.path(Protocol.PlayerVolume).asText()));
    }
}
