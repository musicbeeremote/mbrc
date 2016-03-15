package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.MuteChangeEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.VolumeChangeEvent;
import com.kelsos.mbrc.interactors.MuteInteractor;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackPositionInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.ui.views.MainView;
import com.kelsos.mbrc.utilities.ErrorHandler;
import com.kelsos.mbrc.utilities.RxBus;
import com.kelsos.mbrc.viewmodels.MainViewModel;
import java.util.concurrent.TimeUnit;
import roboguice.inject.ContextSingleton;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@ContextSingleton public class MainViewPresenterImpl implements MainViewPresenter {
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
  @Inject private PlayerStateInteractor playerStateInteractor;

  @Inject private RxBus bus;

  private MainView mainView;
  private Subscription positionUpdate;

  public MainViewPresenterImpl() {
  }

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
    loadPlayerState();
    model.loadComplete();
    subscribe();
  }

  private void subscribe() {
    bus.registerOnMain(this, VolumeChangeEvent.class, this::onVolumeChangedEvent);
    bus.registerOnMain(this, RepeatChange.class, this::onRepeatChangedEvent);
    bus.registerOnMain(this, TrackInfoChangeEvent.class, this::onTrackInfoChangedEvent);
    bus.registerOnMain(this, CoverChangedEvent.class, this::onCoverChangedEvent);
    bus.registerOnMain(this, PlayStateChange.class, this::onPlayStateChanged);
    bus.registerOnMain(this, MuteChangeEvent.class, this::onMuteChanged);
  }

  private void loadPlayerState() {
    if (model.isLoaded()) {
      mainView.updatePlayState(model.getPlayState());
    } else {
      playerStateInteractor.getState().doOnNext(model::setPlayState).subscribe(state -> {
        mainView.updatePlayState(state);
        updatePlaystate(state);
      }, errorHandler::handleThrowable);
    }
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
    if (model.isLoaded()) {
      mainView.updateShuffle(model.getShuffle());
    } else {
      shuffleInteractor.getShuffle().subscribe(shuffle -> {
        model.setShuffle(shuffle);
        mainView.updateShuffle(shuffle);
      }, errorHandler::handleThrowable);
    }
  }

  private void loadRepeat() {
    if (model.isLoaded()) {
      mainView.updateRepeat(model.getRepeat());
    } else {
      repeatInteractor.getRepeat().doOnNext(model::setRepeat).subscribe(repeat -> {
        mainView.updateRepeat(repeat);
      }, errorHandler::handleThrowable);
    }
  }

  private void loadVolume() {
    if (model.isLoaded()) {
      mainView.updateVolume(model.getVolume());
    } else {
      volumeInteractor.getVolume().doOnNext(model::setVolume).subscribe(volume -> {
        mainView.updateVolume(volume);
      }, errorHandler::handleThrowable);
    }
  }

  private void loadPosition() {
    if (model.isLoaded()) {
      mainView.updatePosition(model.getPosition());
    } else {
      positionInteractor.getPosition().doOnNext(model::setPosition).subscribe(position -> {
        mainView.updatePosition(position);
      }, errorHandler::handleThrowable);
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
    playerInteractor.performAction(action)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {

        }, errorHandler::handleThrowable);
  }

  @Override public void onStopPressed() {
    performAction(PlayerAction.STOP);
  }

  @Override public void onMutePressed() {
    muteInteractor.toggle().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(isMute -> {
      model.setMuted(isMute);
      mainView.updateMute(isMute);
    }, errorHandler::handleThrowable);
  }

  @Override public void onShufflePressed() {
    shuffleInteractor.updateShuffle(Shuffle.TOGGLE).subscribe(shuffle -> {
      model.setShuffle(shuffle);
      mainView.updateShuffle(shuffle);
    }, errorHandler::handleThrowable);
  }

  @Override public void onRepeatPressed() {
    repeatInteractor.setRepeat(Repeat.CHANGE)
        .subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mainView::updateRepeat, errorHandler::handleThrowable);
  }

  @Override public void onVolumeChange(int volume) {
    volumeInteractor.setVolume(volume)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(model::setVolume, errorHandler::handleThrowable);
  }

  @Override public void onPositionChange(int position) {
    stopPositionUpdate();
    updatePosition(positionInteractor.setPosition(position));
  }

  public void updatePosition(Observable<TrackPosition> positionObservable) {
    positionObservable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(model::setPosition)
        .subscribe((newPosition) -> {
          startPositionUpdate();
        }, errorHandler::handleThrowable);
  }

  @Override public void onScrobbleToggle() {

  }

  @Override public void onLfmLoveToggle() {

  }

  public void onVolumeChangedEvent(VolumeChangeEvent event) {
    model.setVolume(event.getVolume());
    mainView.updateVolume(event.getVolume());
  }

  public void onRepeatChangedEvent(RepeatChange event) {
    model.setRepeat(event.getMode());
    mainView.updateRepeat(event.getMode());
  }

  public void onTrackInfoChangedEvent(TrackInfoChangeEvent event) {
    Timber.v("Received change event");
    model.setTrackInfo(event.getTrackInfo());
    mainView.updateTrackInfo(event.getTrackInfo());
    startPositionUpdate();
    updatePosition(positionInteractor.getPosition());
  }

  public void onCoverChangedEvent(CoverChangedEvent event) {
    model.setTrackCover(event.getCover());
    mainView.updateCover(event.getCover());
  }

  public void onPlayStateChanged(PlayStateChange event) {
    model.setPlayState(event.getState());
    mainView.updatePlayState(event.getState());
    updatePlaystate(event.getState());
  }

  private void updatePlaystate(String state) {
    if (PlayerState.PLAYING.equals(state)) {
      startPositionUpdate();
    } else {
      stopPositionUpdate();
    }
  }

  private void stopPositionUpdate() {
    Timber.v("Track now is either paused or stoped");
    if (positionUpdate != null && !positionUpdate.isUnsubscribed()) {
      positionUpdate.unsubscribe();
      positionUpdate = null;
    }
  }

  private void startPositionUpdate() {
    if (positionUpdate != null) {
      return;
    }

    Timber.v("Track is now playing");
    positionUpdate = Observable.interval(0, 1, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .takeWhile(aLong1 -> {
          final TrackPosition position = model.getPosition();
          return position.getCurrent() < position.getTotal();
        })
        .subscribe(aLong -> {
          final TrackPosition modelPosition = model.getPosition();
          TrackPosition newPosition = new TrackPosition(modelPosition.getCurrent() + 1000, modelPosition.getTotal());
          model.setPosition(newPosition);
          mainView.updatePosition(newPosition);
        }, errorHandler::handleThrowable);
  }

  public void onMuteChanged(MuteChangeEvent event) {
    model.setMuted(event.isMute());
    mainView.updateMute(event.isMute());
  }
}
