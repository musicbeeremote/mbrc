package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateLyrics implements ICommand {
    private MainDataModel model;

    @Inject public UpdateLyrics(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setLyrics(e.getDataString());
    }
}
