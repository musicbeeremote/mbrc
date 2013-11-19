package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.MainDataModel;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;

public class UpdateRepeat implements ICommand {
    private MainDataModel model;

    @Inject public UpdateRepeat(MainDataModel model) {
        this.model = model;
    }

    @Override
    public void execute(IEvent e) {
        model.setRepeatState(e.getDataString());
    }
}
