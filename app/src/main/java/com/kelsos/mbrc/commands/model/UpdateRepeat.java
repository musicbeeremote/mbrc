package com.kelsos.mbrc.commands.model;

import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateRepeat implements ICommand {
  private MainDataModel model;

  @Inject public UpdateRepeat(MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(IEvent e) {
    model.setRepeatState(e.getDataString());
  }
}
