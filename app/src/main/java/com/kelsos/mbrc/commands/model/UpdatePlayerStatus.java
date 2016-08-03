package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdatePlayerStatus implements ICommand {
  private MainDataModel model;

  @Inject public UpdatePlayerStatus(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    ObjectNode node = (ObjectNode) e.getData();
    model.setPlayState(node.path(Protocol.PlayerState).asText());
    model.setMuteState(node.path(Protocol.PlayerMute).asBoolean());
    model.setRepeatState(node.path(Protocol.PlayerRepeat).asText());
    final String shuffleState = node.path(Protocol.PlayerShuffle).asText();
    //noinspection ResourceType
    model.setShuffleState(shuffleState);
    model.setScrobbleState(node.path(Protocol.PlayerScrobble).asBoolean());
    model.setVolume(Integer.parseInt(node.path(Protocol.PlayerVolume).asText()));
  }
}
