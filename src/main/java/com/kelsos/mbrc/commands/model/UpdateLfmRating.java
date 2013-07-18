package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateLfmRating implements ICommand{
    private MainDataModel model;

    @Inject public UpdateLfmRating(MainDataModel model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        model.setLfmRating(e.getDataString());
    }
}
