package com.kelsos.mbrc.commands.visual;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.inject.Inject;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdateNowPlayingTrackMoved implements ICommand {
  private RxBus bus;

  @Inject public UpdateNowPlayingTrackMoved(RxBus bus) {
    this.bus = bus;
  }

  @Override public void execute(IEvent e) {
    bus.post(new TrackMoved((ObjectNode) e.getData()));
  }
}
