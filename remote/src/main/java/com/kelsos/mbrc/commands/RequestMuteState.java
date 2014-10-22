package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.node.BooleanNode;

public class RequestMuteState implements ICommand {
    private Model model;

    @Inject public RequestMuteState(Model model) {
        this.model = model;
    }

    @Override
    public void execute(IEvent e) {
        model.setMuteState(((BooleanNode) e.getData()).asBoolean());
    }
}
