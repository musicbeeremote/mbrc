package com.kelsos.mbrc.commands.visual;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.TrackRemoval;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdateNowPlayingTrackRemoval implements ICommand {
  private RxBus bus;

  @Inject public UpdateNowPlayingTrackRemoval(RxBus bus) {
    this.bus = bus;
  }

  @Override public void execute(final IEvent e) {
    e.getData();
    bus.post(new TrackRemoval((ObjectNode) e.getData()));
  }
}
