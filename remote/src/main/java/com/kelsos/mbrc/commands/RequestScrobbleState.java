package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.node.BooleanNode;

public class RequestScrobbleState implements ICommand {
    private Model model;

    @Inject public RequestScrobbleState(Model model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setScrobbleState(((BooleanNode) e.getData()).asBoolean());
    }
}
