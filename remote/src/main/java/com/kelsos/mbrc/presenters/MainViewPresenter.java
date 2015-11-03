package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.VolumeChangeEvent;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.models.MainViewModel;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.ui.views.MainView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton
public class MainViewPresenter implements IMainViewPresenter {
  @Inject private MainViewModel model;
  @Inject private PlayerInteractor playerInteractor;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private ShuffleInteractor shuffleInteractor;
  @Inject private RepeatInteractor repeatInteractor;

  @Inject private TrackRepository trackRepository;
  @Inject private PlayerRepository playerRepository;
  @Inject private Bus bus;

  private MainView mainView;

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
      trackRepository.getTrackInfo(false).subscribe(trackInfo -> {
        model.setTrackInfo(trackInfo);
        mainView.updateTrackInfo(trackInfo);
      });
    } else {
      mainView.updateTrackInfo(model.getTrackInfo());
    }
  }

  private void loadCover() {
    if (model.getTrackCover() == null) {
      trackRepository.getTrackCover().subscribe(bitmap -> {
        model.setTrackCover(bitmap);
        mainView.updateCover(bitmap);
      }, Ln::v);
    } else {
      mainView.updateCover(model.getTrackCover());
    }
  }

  private void loadShuffle() {
    if (model.getShuffle() == null) {
      playerRepository.getShuffleState().subscribe(shuffle -> {
        model.setShuffle(shuffle);
        mainView.updateShuffle(shuffle.getState());
      });
    } else {
      mainView.updateShuffle(model.getShuffle().getState());
    }
  }

  private void loadRepeat() {
    if (model.getRepeat() == null) {
      playerRepository.getRepeat(false).subscribe(repeat -> {
        model.setRepeat(repeat);
        mainView.updateRepeat(repeat);
      });
    } else {
      mainView.updateRepeat(model.getRepeat());
    }
  }

  private void loadVolume() {
    if (model.getVolume() == null) {
      playerRepository.getVolume().subscribe(volume -> {
        model.setVolume(volume);
        mainView.updateVolume(volume.getValue());
      });
    } else {
      mainView.updateVolume(model.getVolume().getValue());
    }
  }

  private void loadPosition() {
    if (model.getPosition() == null) {
      trackRepository.getPosition().subscribe(position -> {
        model.setPosition(position);
        mainView.updatePosition(new TrackPosition(position.getPosition(), position.getDuration()));
      });
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

        });
  }


  @Override public void onStopPressed() {
    performAction(PlayerAction.STOP);
  }

  @Override public void onMutePressed() {

  }

  @Override public void onShufflePressed() {

  }

  @Override public void onRepeatPressed() {
    repeatInteractor.execute(false).subscribeOn(Schedulers.io())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mainView::updateRepeat, Ln::v);
  }

  @Override public void onVolumeChange(int volume) {
    volumeInteractor.execute(volume)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playerRepository::setVolume);
  }

  @Override public void onPositionChange(int position) {

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

}
