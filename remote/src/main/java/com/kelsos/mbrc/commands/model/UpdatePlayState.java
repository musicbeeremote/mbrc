package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdatePlayState implements ICommand {
    private MainDataModel model;

    @Inject public UpdatePlayState(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setPlayState(e.getDataString());
    }
}
