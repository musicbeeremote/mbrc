package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.node.BooleanNode;

public class RequestMuteState implements ICommand {
    private MainDataModel model;

    @Inject public RequestMuteState(MainDataModel model) {
        this.model = model;
    }

    @Override
    public void execute(IEvent e) {
        model.setMuteState(((BooleanNode) e.getData()).asBoolean());
    }
}
