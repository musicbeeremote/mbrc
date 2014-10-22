package com.kelsos.mbrc.rest;

import com.google.inject.Inject;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.events.actions.*;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
        api.updateVolume(event.getVolume());
    }

    @Subscribe
    public void handlePlayPressed(PlayPressedEvent event) {
        api.playbackStart();
    }

    @Subscribe
    public void handlePreviousPressed(PreviousPressedEvent event) {
        api.playPrevious();
    }

    @Subscribe
    public void handleNextPressed(NextPressedEvent event) {
        api.playNext();
    }

    @Subscribe
    public void handleStopPressed(StopPressedEvent event) {
        api.playbackStop();
    }

    @Subscribe
    public void handleShufflePressed(ShufflePressedEvent event) {
        api.updateShuffleState(!model.isShuffleActive());
    }

    @Subscribe
    public void handleRepeatPressed(RepeatChangeEvent event) {
        final String mode = model.isRepeatActive() ? "none" : "all";
        api.updateRepeatState(mode);
    }


}
