package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class RequestRepeatState implements ICommand {
    private Model model;

    @Inject public RequestRepeatState(Model model) {
        this.model = model;
    }

    @Override
    public void execute(IEvent e) {
        model.setRepeatState(e.getDataString());
    }
}
