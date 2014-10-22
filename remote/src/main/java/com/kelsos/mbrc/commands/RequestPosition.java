package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RequestPosition implements ICommand {

    @Inject
    private RemoteApi api;

    private MainThreadBusWrapper bus;

    @Inject
    public RequestPosition(MainThreadBusWrapper bus) {
        this.bus = bus;
    }

    public void execute(IEvent e) {

        api.getCurrentPosition()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> bus.post(new UpdatePosition(resp.getPosition(), resp.getDuration())));
    }
}
