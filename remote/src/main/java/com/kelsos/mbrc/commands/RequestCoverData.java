package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.MainThreadBusWrapper;

public class RequestCoverData implements ICommand {
    private MainThreadBusWrapper bus;
    @Inject
    public RequestCoverData(MainThreadBusWrapper bus) {
        this.bus = bus;
    }

    @Override
    public void execute(IEvent e) {

        bus.post(new CoverAvailable(RemoteApi.COVER_URL));
    }
}
