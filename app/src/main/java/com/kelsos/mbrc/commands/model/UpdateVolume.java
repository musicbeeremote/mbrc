package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.IntNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateVolume implements ICommand {
  private MainDataModel model;

  @Inject public UpdateVolume(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setVolume(((IntNode) e.getData()).asInt());
  }
}
