package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.presenters.interfaces.IMiniControlPresenter;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.kelsos.mbrc.utilities.ErrorHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.inject.ContextSingleton;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton
public class MiniControlPresenter implements IMiniControlPresenter {
  private MiniControlView view;
  @Inject private Bus bus;
  @Inject private PlayerInteractor interactor;
  @Inject private ErrorHandler handler;

  @Override public void onNextPressed() {
    action(PlayerAction.NEXT);
  }

  private void action(@PlayerAction.Action String action) {
    interactor.execute(action)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(baseResponse -> {}, handler::handleThrowable);
  }

  @Override public void onPreviousPressed() {
    action(PlayerAction.PREVIOUS);
  }

  @Override public void onPlayPause() {
    action(PlayerAction.PLAY_PLAUSE);
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

  @Subscribe public void onCoverAvailable(CoverChangedEvent event) {
    view.updateCover(event.getCover());
  }

  @Subscribe public void onPlayStateChange(PlayStateChange event) {
    view.updatePlayerState(event.getState().getValue());
  }

  @Subscribe public void onTrackInfoChange(TrackInfoChangeEvent event) {
    final TrackInfo info = event.getTrackInfo();
    view.updateTrack(info.getArtist(), info.getAlbum());
  }
}
