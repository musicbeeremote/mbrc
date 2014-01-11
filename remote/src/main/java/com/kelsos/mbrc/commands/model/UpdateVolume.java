package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import org.codehaus.jackson.node.IntNode;

public class UpdateVolume implements ICommand {
    private MainDataModel model;

    @Inject public UpdateVolume(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setVolume(((IntNode) e.getData()).asInt());
    }
}
