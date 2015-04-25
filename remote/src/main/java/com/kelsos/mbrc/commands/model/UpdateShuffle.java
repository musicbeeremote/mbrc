package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.JsonNode;

public class UpdateShuffle implements ICommand {
  private MainDataModel model;

  @Inject public UpdateShuffle(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    String data = e.getDataString();

    // Older plugin support, where the shuffle had boolean value.
    if (data == null) {
      data = ((JsonNode)e.getData()).asBoolean() ? ShuffleChange.SHUFFLE : ShuffleChange.OFF;
    }

    //noinspection ResourceType
    model.setShuffleState(data);
  }
}
