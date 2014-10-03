package com.kelsos.mbrc.rest;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.actions.*;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RemoteClient {

    private static final Logger logger = LoggerManager.getLogger();

    private Bus bus;
    private RemoteApi api;

    @Inject
    public RemoteClient(Bus bus, RemoteApi api) {
        this.bus = bus;
        this.api = api;
        this.bus.register(this);
    }

    @Subscribe
    public void handleVolumeChange(ChangeVolumeEvent event) {

        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };

        api.updateVolume(event.getVolume(), cb);
    }

    @Subscribe
    public void handlePlayPressed(PlayPressedEvent event) {
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        api.playbackStart(cb);
    }

    @Subscribe
    public void handlePreviousPressed(PreviousPressedEvent event) {
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        api.playPrevious(cb);
    }

    @Subscribe
    public void handleNextPressed(NextPressedEvent event) {
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        api.playNext(cb);
    }

    @Subscribe
    public void handleStopPressed(StopPressedEvent event) {
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        api.playbackStop(cb);
    }

    @Subscribe
    public void handleShufflePressed(ShufflePressedEvent event) {
        logger.d("Event Received for shuffle button");
    }

    @Subscribe
    public void handleRepeatPressed(RepeatChangeEvent event) {
        logger.d("Event Received for repeat button");
    }
}
