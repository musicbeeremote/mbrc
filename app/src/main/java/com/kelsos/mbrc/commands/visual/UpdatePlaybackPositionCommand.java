package com.kelsos.mbrc.commands.visual;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.inject.Inject;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdatePlaybackPositionCommand implements ICommand {
  private RxBus bus;

  @Inject public UpdatePlaybackPositionCommand(RxBus bus) {
    this.bus = bus;
  }

  public void execute(IEvent e) {
    ObjectNode oNode = (ObjectNode) e.getData();
    bus.post(new UpdatePosition(oNode.path("current").asInt(), oNode.path("total").asInt()));
  }
}
