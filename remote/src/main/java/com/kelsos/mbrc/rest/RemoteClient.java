package com.kelsos.mbrc.rest;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
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
    private Model model;

    @Inject
    public RemoteClient(Bus bus, RemoteApi api, Model model) {
        this.bus = bus;
        this.api = api;
        this.bus.register(this);
        this.model = model;
    }

    @Subscribe
    public void handleVolumeChange(VolumeEvent event) {

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
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        api.updateShuffleState(!model.isShuffleActive(), cb);
    }

    @Subscribe
    public void handleRepeatPressed(RepeatChangeEvent event) {
        final Callback<SuccessResponse> cb = new Callback<SuccessResponse>() {
            @Override
            public void success(SuccessResponse successResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        final String mode = model.isRepeatActive() ? "none" : "all";
        api.updateRepeatState(mode, cb);
    }


}
