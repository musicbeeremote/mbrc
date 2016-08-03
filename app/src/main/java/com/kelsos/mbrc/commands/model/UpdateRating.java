package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.TextNode;
import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateRating implements ICommand {
  private MainDataModel model;

  @Inject public UpdateRating(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setRating(((TextNode) e.getData()).asDouble(0));
  }
}
