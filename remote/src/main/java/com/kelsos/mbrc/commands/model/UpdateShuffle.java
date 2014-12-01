package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.node.BooleanNode;

public class UpdateShuffle implements ICommand {
    private MainDataModel model;

    @Inject public UpdateShuffle(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setShuffleState(((BooleanNode) e.getData()).asBoolean());
    }
}
