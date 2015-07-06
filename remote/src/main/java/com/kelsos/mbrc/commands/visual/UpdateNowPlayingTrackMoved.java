package com.kelsos.mbrc.commands.visual;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.TrackMoved;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;

public class UpdateNowPlayingTrackMoved implements ICommand {
  private MainThreadBusWrapper bus;

  @Inject public UpdateNowPlayingTrackMoved(MainThreadBusWrapper bus) {
    this.bus = bus;
  }

  @Override public void execute(IEvent e) {
    bus.post(new TrackMoved((ObjectNode) e.getData()));
  }
}
