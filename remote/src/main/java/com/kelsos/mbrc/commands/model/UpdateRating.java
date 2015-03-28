package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.node.TextNode;

public class UpdateRating implements ICommand {
  private MainDataModel model;

  @Inject public UpdateRating(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setRating(((TextNode) e.getData()).asDouble(0));
  }
}
