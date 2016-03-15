package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.ui.views.MiniControlView;
import com.kelsos.mbrc.utilities.ErrorHandler;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.viewmodels.MiniControlModel;
import roboguice.inject.ContextSingleton;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton public class MiniControlPresenterImpl implements MiniControlPresenter {
  private MiniControlView view;
  @Inject private RxBus bus;
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
    interactor.performAction(action)
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
    bus.registerOnMain(this, CoverChangedEvent.class, this::onCoverAvailable);
    bus.registerOnMain(this, PlayStateChange.class, this::onPlayStateChange);
    bus.registerOnMain(this, TrackInfoChangeEvent.class, this::onTrackInfoChange);
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

    playerStateInteractor.getState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(model::setPlayerState)
        .subscribe(playState -> {
          view.updatePlayerState(playState);
        }, handler::handleThrowable);
  }

  public void onCoverAvailable(CoverChangedEvent event) {
    view.updateCover(event.getCover());
  }

  public void onPlayStateChange(PlayStateChange event) {
    view.updatePlayerState(event.getState());
  }

  public void onTrackInfoChange(TrackInfoChangeEvent event) {
    final TrackInfo info = event.getTrackInfo();
    view.updateTrack(info.getArtist(), info.getTitle());
  }
}
