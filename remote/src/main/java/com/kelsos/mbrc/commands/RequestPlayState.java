package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class RequestPlayState implements ICommand {
    private MainDataModel model;

    @Inject public RequestPlayState(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setPlayState(e.getDataString());
    }
}
