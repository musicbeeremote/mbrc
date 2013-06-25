package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import org.codehaus.jackson.node.BooleanNode;

public class UpdateMute implements ICommand {
    @Inject
    MainDataModel model;
    @Override
    public void execute(IEvent e) {
        model.setMuteState(((BooleanNode)e.getData()).asBoolean());
    }
}
