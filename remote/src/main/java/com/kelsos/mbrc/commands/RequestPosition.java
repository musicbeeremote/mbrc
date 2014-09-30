package com.kelsos.mbrc.commands;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.TrackPositionResponse;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestPosition implements ICommand {

    @Inject
    private RemoteApi api;

    private MainThreadBusWrapper bus;

    @Inject public RequestPosition(MainThreadBusWrapper bus) {
        this.bus = bus;
    }

    public void execute(IEvent e) {
        api.getCurrentPosition(new Callback<TrackPositionResponse>() {
            @Override
            public void success(TrackPositionResponse trackPositionResponse, Response response) {
                int position = trackPositionResponse.getPosition();
                int duration = trackPositionResponse.getDuration();
                UpdatePosition event = new UpdatePosition(position, duration);
                bus.post(event);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }
}
