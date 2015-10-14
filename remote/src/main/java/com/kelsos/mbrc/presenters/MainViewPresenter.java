package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.annotations.RepeatMode;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.models.MainViewModel;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.ui.views.MainView;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton
public class MainViewPresenter implements IMainViewPresenter {
  @Inject private MainViewModel model;
  @Inject private PlayerInteractor actionUserCase;
  @Inject private TrackRepository trackRepository;
  @Inject private PlayerRepository playerRepository;
  private MainView mainView;

  @Override public void bind(MainView mainView) {
    this.mainView = mainView;
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {
    loadTrackInfo();
    loadCover();
    loadShuffle();
    loadRepeat();
    loadVolume();
  }

  private void loadTrackInfo() {
    if (model.getTrackInfo() == null) {
      trackRepository.getTrackInfo().subscribe(trackInfo -> {
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
      }, throwable -> {
        Ln.v(throwable);
      });
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
      playerRepository.getRepeat().subscribe(repeat -> {
        model.setRepeat(repeat);
        mainView.updateRepeat(RepeatMode.ALL.equals(repeat.getValue()));
      });
    } else {
      mainView.updateRepeat(RepeatMode.ALL.equals(model.getRepeat().getValue()));
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

  @Override public void onPlayPausePressed() {
    performAction(PlaybackAction.PLAY_PLAUSE);
  }

  @Override public void onPreviousPressed() {
    performAction(PlaybackAction.PREVIOUS);
  }

  @Override public void onNextPressed() {
    performAction(PlaybackAction.NEXT);
  }

  private void performAction(String action) {
    actionUserCase.execute(action).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {

        });
  }


  @Override public void onStopPressed() {
    performAction(PlaybackAction.STOP);
  }

  @Override public void onMutePressed() {

  }

  @Override public void onShufflePressed() {

  }

  @Override public void onRepeatPressed() {

  }

  @Override public void onVolumeChange(int volume) {

  }

  @Override public void onPositionChange(int position) {

  }

  @Override public void onScrobbleToggle() {

  }

  @Override public void onLfmLoveToggle() {

  }

}
