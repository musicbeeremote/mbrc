package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdatePluginVersionCommand implements ICommand {
    private MainDataModel model;

    @Inject public UpdatePluginVersionCommand(final MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(final IEvent e) {
        model.setPluginVersion(e.getDataString());
    }
}
