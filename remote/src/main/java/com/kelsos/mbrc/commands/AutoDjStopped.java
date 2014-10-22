package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class AutoDjStopped implements ICommand {
    private Model model;

    @Inject public AutoDjStopped(Model model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setLfmRating(e.getDataString());
    }
}
