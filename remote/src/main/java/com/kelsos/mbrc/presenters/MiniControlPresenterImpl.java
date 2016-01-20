package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.models.MiniControlModel;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.kelsos.mbrc.utilities.ErrorHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import roboguice.inject.ContextSingleton;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton public class MiniControlPresenterImpl implements MiniControlPresenter {
  private MiniControlView view;
  @Inject private Bus bus;
  @Inject private PlayerInteractor interactor;
  @Inject private ErrorHandler handler;
  @Inject private MiniControlModel model;
  @Inject private TrackCoverInteractor coverInteractor;
  @Inject private TrackInfoInteractor infoInteractor;
  @Inject private PlayerStateInteractor playerStateInteractor;

  @Override public void onNextPressed() {
    action(PlayerAction.NEXT);
  }

  private void action(@PlayerAction.Action String action) {
    interactor.execute(action)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(baseResponse -> {
        }, handler::handleThrowable);
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

  @Override public void load() {
    coverInteractor.execute(false)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(bitmap -> {
          view.updateCover(bitmap);
          model.setCover(bitmap);
        }, handler::handleThrowable);

    infoInteractor.execute(false)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(trackInfo -> {
          view.updateTrack(trackInfo.getArtist(), trackInfo.getTitle());
          model.setArtist(trackInfo.getArtist());
          model.setTitle(trackInfo.getTitle());
        }, handler::handleThrowable);

    playerStateInteractor.execute(false)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playState -> {
          model.setPlayerState(playState.getValue());
          view.updatePlayerState(playState.getValue());
        }, handler::handleThrowable);

  }

  @Subscribe public void onCoverAvailable(CoverChangedEvent event) {
    view.updateCover(event.getCover());
  }

  @Subscribe public void onPlayStateChange(PlayStateChange event) {
    view.updatePlayerState(event.getState()
        .getValue());
  }

  @Subscribe public void onTrackInfoChange(TrackInfoChangeEvent event) {
    final TrackInfo info = event.getTrackInfo();
    view.updateTrack(info.getArtist(), info.getTitle());
  }
}
