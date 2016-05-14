package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.BooleanNode;
import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateMute implements ICommand {
  private MainDataModel model;

  @Inject public UpdateMute(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setMuteState(((BooleanNode) e.getData()).asBoolean());
  }
}
