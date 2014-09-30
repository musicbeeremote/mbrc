package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.util.MainThreadBusWrapper;

public class AutoDjStarted implements ICommand {
    private MainThreadBusWrapper bus;

    @Inject public AutoDjStarted(MainThreadBusWrapper bus) {
        this.bus = bus;
    }

    @Override public void execute(final IEvent e) {

    }
}
