package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.TrackResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestTrackData implements ICommand {
    private Model model;

    @Inject
    private RemoteApi api;

    @Inject public RequestTrackData(Model model) {
        this.model = model;
    }

    @Override public void execute(IEvent e) {
        api.getTrackInfo(new Callback<TrackResponse>() {
            @Override
            public void success(TrackResponse trackResponse, Response response) {
                model.setTrackInfo(trackResponse.getArtist(),
                        trackResponse.getAlbum(),
                        trackResponse.getTitle(),
                        trackResponse.getYear());
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
