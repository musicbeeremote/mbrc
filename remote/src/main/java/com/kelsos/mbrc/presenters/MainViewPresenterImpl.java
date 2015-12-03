package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.MuteChangeEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.VolumeChangeEvent;
import com.kelsos.mbrc.interactors.MuteInteractor;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackPositionInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.models.MainViewModel;
import com.kelsos.mbrc.presenters.interfaces.MainViewPresenter;
import com.kelsos.mbrc.ui.views.MainView;
import com.kelsos.mbrc.utilities.ErrorHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.util.concurrent.TimeUnit;
import roboguice.inject.ContextSingleton;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton
public class MainViewPresenterImpl implements MainViewPresenter {
  @Inject private ErrorHandler errorHandler;
  @Inject private MainViewModel model;
  @Inject private PlayerInteractor playerInteractor;
  @Inject private TrackInfoInteractor trackInfoInteractor;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private ShuffleInteractor shuffleInteractor;
  @Inject private RepeatInteractor repeatInteractor;
  @Inject private MuteInteractor muteInteractor;
  @Inject private TrackCoverInteractor coverInteractor;
  @Inject private TrackPositionInteractor positionInteractor;

  @Inject private Bus bus;

  private MainView mainView;
  private Subscription positionUpdate;

  @Override public void bind(MainView mainView) {
    this.mainView = mainView;
  }

  @Override public void onPause() {
    bus.unregister(this);
  }

  @Override public void onResume() {
    loadTrackInfo();
    loadCover();
    loadShuffle();
    loadRepeat();
    loadVolume();
    loadPosition();
    bus.register(this);
  }

  private void loadTrackInfo() {
    if (model.getTrackInfo() == null) {
      trackInfoInteractor.execute(false).subscribe(trackInfo -> {
        model.setTrackInfo(trackInfo);
        mainView.updateTrackInfo(trackInfo);
      }, errorHandler::handleThrowable);
    } else {
      mainView.updateTrackInfo(model.getTrackInfo());
    }
  }

  private void loadCover() {
    if (model.getTrackCover() == null) {
      coverInteractor.execute(false).subscribe(bitmap -> {
        model.setTrackCover(bitmap);
        mainView.updateCover(bitmap);
      }, errorHandler::handleThrowable);
    } else {
      mainView.updateCover(model.getTrackCover());
    }
  }

  private void loadShuffle() {
    if (model.getShuffle() == null) {
      shuffleInteractor.execute().subscribe(shuffle -> {
        model.setShuffle(shuffle);
        mainView.updateShuffle(shuffle.getState());
      }, errorHandler::handleThrowable);
    } else {
      mainView.updateShuffle(model.getShuffle()
          .getState());
    }
  }

  private void loadRepeat() {
    if (model.getRepeat() == null) {
      repeatInteractor.execute(false).subscribe(repeat -> {
        model.setRepeat(repeat);
        mainView.updateRepeat(repeat);
      }, errorHandler::handleThrowable);
    } else {
      mainView.updateRepeat(model.getRepeat());
    }
  }

  private void loadVolume() {
    if (model.getVolume() == null) {
      volumeInteractor.execute(false).subscribe(volume -> {
        model.setVolume(volume);
        mainView.updateVolume(volume.getValue());
      }, errorHandler::handleThrowable);
    } else {
      mainView.updateVolume(model.getVolume()
          .getValue());
    }
  }

  private void loadPosition() {
    if (model.getPosition() == null) {
      positionInteractor.execute().subscribe(position -> {
        model.setPosition(position);
        mainView.updatePosition(new TrackPosition(position.getPosition(), position.getDuration()));
      }, errorHandler::handleThrowable);
    } else {
      Position position = model.getPosition();
      mainView.updatePosition(new TrackPosition(position.getPosition(), position.getDuration()));
    }
  }

  @Override public void onPlayPausePressed() {
    performAction(PlayerAction.PLAY_PLAUSE);
  }

  @Override public void onPreviousPressed() {
    performAction(PlayerAction.PREVIOUS);
  }

  @Override public void onNextPressed() {
    performAction(PlayerAction.NEXT);
  }

  private void performAction(String action) {
    playerInteractor.execute(action).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {

        }, errorHandler::handleThrowable);
  }


  @Override public void onStopPressed() {
    performAction(PlayerAction.STOP);
  }

  @Override public void onMutePressed() {
    muteInteractor.execute().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(isMute -> {
          model.setMuted(isMute);
          mainView.updateMute(isMute);
        }, errorHandler::handleThrowable);
  }

  @Override public void onShufflePressed() {
    shuffleInteractor.execute(Shuffle.TOGGLE).subscribe(shuffle -> {
      model.setShuffle(shuffle);
      mainView.updateShuffle(shuffle.getState());
    }, errorHandler::handleThrowable);
  }

  @Override public void onRepeatPressed() {
    repeatInteractor.execute(Repeat.CHANGE).subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mainView::updateRepeat, errorHandler::handleThrowable);
  }

  @Override public void onVolumeChange(int volume) {
    volumeInteractor.execute(volume)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(model::setVolume, errorHandler::handleThrowable);
  }

  @Override public void onPositionChange(int position) {
    stopPositionUpdate();
    positionInteractor.execute()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((position1) -> {
          model.setPosition(position1);
          startPositionUpdate();
        }, errorHandler::handleThrowable);
  }

  @Override public void onScrobbleToggle() {

  }

  @Override public void onLfmLoveToggle() {

  }

  @Subscribe public void onVolumeChangedEvent(VolumeChangeEvent event) {
    final Volume volume = event.getVolume();
    model.setVolume(volume);
    mainView.updateVolume(volume.getValue());
  }

  @Subscribe public void onRepeatChangedEvent(RepeatChange event) {
    model.setRepeat(event.getMode());
    mainView.updateRepeat(event.getMode());
  }

  @Subscribe public void onTrackInfoChangedEvent(TrackInfoChangeEvent event) {
    model.setTrackInfo(event.getTrackInfo());
    mainView.updateTrackInfo(event.getTrackInfo());
  }

  @Subscribe public void onCoverChangedEvent(CoverChangedEvent event) {
    model.setTrackCover(event.getCover());
    mainView.updateCover(event.getCover());
  }

  @Subscribe public void onPlayStateChanged(PlayStateChange event) {
    model.setPlayState(event.getState());
    mainView.updatePlayState(event.getState());

    final String state = event.getState().getValue();
    updatePlaystate(state);
  }

  private void updatePlaystate(String state) {
    if (PlayerState.PLAYING.equals(state)) {
      startPositionUpdate();
    } else {
      stopPositionUpdate();
    }
  }

  private void stopPositionUpdate() {
    if (positionUpdate != null && !positionUpdate.isUnsubscribed()) {
      positionUpdate.unsubscribe();
      positionUpdate = null;
    }
  }

  private void startPositionUpdate() {
    if (positionUpdate != null) {
      return;
    }

    positionUpdate = Observable.interval(0, 1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .takeWhile(aLong1 -> {
          final Position position = model.getPosition();
          return position.getPosition() < position.getDuration();
        })
        .subscribe(aLong -> {
          final Position modelPosition = model.getPosition();
          final int position = modelPosition.getPosition();
          modelPosition.setPosition(position + 1000);
          model.setPosition(modelPosition);
          mainView.updatePosition(
              new TrackPosition(modelPosition.getPosition(), modelPosition.getDuration()));
        }, errorHandler::handleThrowable);
  }

  @Subscribe public void onMuteChanged(MuteChangeEvent event) {
    model.setMuted(event.isMute());
    mainView.updateMute(event.isMute());
  }

}
