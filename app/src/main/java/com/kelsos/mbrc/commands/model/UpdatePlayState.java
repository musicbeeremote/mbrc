package com.kelsos.mbrc.commands.model;

import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdatePlayState implements ICommand {
  private MainDataModel model;

  @Inject public UpdatePlayState(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setPlayState(e.getDataString());
  }
}
