package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.presenters.interfaces.IMiniControlPresenter;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.inject.ContextSingleton;

@ContextSingleton
public class MiniControlPresenter implements IMiniControlPresenter {
  private MiniControlView view;
  @Inject private Bus bus;

  @Override public void onNextPressed() {

  }

  @Override public void onPreviousPressed() {

  }

  @Override public void onPlayPause() {

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
