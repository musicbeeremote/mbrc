package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.net.Protocol;
import org.codehaus.jackson.node.ObjectNode;

public class UpdatePlayerStatus implements ICommand {
    private MainDataModel model;

    @Inject public UpdatePlayerStatus(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        ObjectNode node = (ObjectNode) e.getData();
        model.setPlayState(node.path(Protocol.PLAYER_STATE).asText());
        model.setMuteState(node.path(Protocol.PLAYER_MUTE).asBoolean());
        model.setRepeatState(node.path(Protocol.PLAYER_REPEAT).asText());
        model.setShuffleState(node.path(Protocol.PLAYER_SHUFFLE).asBoolean());
        model.setScrobbleState(node.path(Protocol.PLAYER_SCROBBLE).asBoolean());
        model.setVolume(Integer.parseInt(node.path(Protocol.PLAYER_VOLUME).asText()));
    }
}
