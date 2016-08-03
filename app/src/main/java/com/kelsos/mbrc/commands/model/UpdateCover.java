package com.kelsos.mbrc.commands.model;

import com.fasterxml.jackson.databind.node.TextNode;
import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateCover implements ICommand {
  private MainDataModel model;

  @Inject public UpdateCover(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setCover(((TextNode) e.getData()).textValue());
  }
}
