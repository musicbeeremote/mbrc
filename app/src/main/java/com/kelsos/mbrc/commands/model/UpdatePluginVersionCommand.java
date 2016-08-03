package com.kelsos.mbrc.commands.model;

import javax.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdatePluginVersionCommand implements ICommand {
  private MainDataModel model;

  @Inject public UpdatePluginVersionCommand(final MainDataModel model) {
    this.model = model;
  }

  @Override public void execute(final IEvent e) {
    model.setPluginVersion(e.getDataString());
  }
}
