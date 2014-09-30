package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class RequestRepeatState implements ICommand {
    private MainDataModel model;

    @Inject public RequestRepeatState(MainDataModel model) {
        this.model = model;
    }

    @Override
    public void execute(IEvent e) {
        model.setRepeatState(e.getDataString());
    }
}
