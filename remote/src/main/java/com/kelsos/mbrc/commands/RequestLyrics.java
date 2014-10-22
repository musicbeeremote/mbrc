package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class RequestLyrics implements ICommand {
    private Model model;

    @Inject public RequestLyrics(Model model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setLyrics(e.getDataString());
    }
}
