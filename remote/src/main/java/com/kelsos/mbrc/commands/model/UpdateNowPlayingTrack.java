package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateNowPlayingTrack implements ICommand {
  private MainDataModel model;

  @Inject public UpdateNowPlayingTrack(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    ObjectNode node = (ObjectNode) e.getData();
    model.setTrackInfo(node.path("Artist").textValue(),
        node.path("Album").textValue(),
        node.path("Title").textValue(),
        node.path("Year").textValue());
  }
}
