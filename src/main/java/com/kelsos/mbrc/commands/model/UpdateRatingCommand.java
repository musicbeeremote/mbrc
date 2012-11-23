package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;

public class UpdateRatingCommand implements ICommand {
    @Inject
    MainDataModel model;

    @Override
    public void execute(IEvent e) {
        model.setRating(e.getData());
    }
}
