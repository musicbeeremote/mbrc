package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RequestTrackData implements ICommand {
    private Model model;

    @Inject
    private RemoteApi api;

    @Inject public RequestTrackData(Model model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {

        api.getTrackInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> model.setTrackInfo(resp.getArtist(),
                        resp.getAlbum(),
                        resp.getTitle(),
                        resp.getYear()));

    }
}
