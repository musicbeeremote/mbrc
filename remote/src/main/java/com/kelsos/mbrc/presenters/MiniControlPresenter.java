package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.PlayerController;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.presenters.interfaces.IMiniControlPresenter;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MiniControlPresenter implements IMiniControlPresenter {
  private MiniControlView view;
  @Inject private Bus bus;
  @Inject private PlayerController controller;

  @Override public void onNextPressed() {
    controller.onNextPressed();
  }

  @Override public void onPreviousPressed() {
    controller.onPreviousPressed();
  }

  @Override public void onPlayPause() {
    controller.onPlayPressed();
  }

  @Override public void bind(MiniControlView view) {
    this.view = view;
  }

  @Override public void onResume() {
    bus.register(this);
  }

  @Override public void onPause() {
    bus.unregister(this);
  }

  @Subscribe public void onCoverAvailable(CoverAvailable event) {
    view.updateCover(event.getCover());
  }

  @Subscribe public void onPlayStateChange(PlayStateChange event) {
    view.updatePlaystate(event.getState());
  }

  @Subscribe public void onTrackInfoChange(TrackInfoChange event) {
    view.updateTrack(event.getArtist(), event.getAlbum());
  }
}
