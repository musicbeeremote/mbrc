package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.models.MainViewModel;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.ui.views.MainView;

import roboguice.inject.ContextSingleton;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@ContextSingleton
public class MainViewPresenter implements IMainViewPresenter {
  @Inject
  private MainViewModel model;
  @Inject private PlayerInteractor actionUserCase;
  @Inject private TrackRepository trackRepository;
  private MainView mainView;

  @Override public void bind(MainView mainView) {
    this.mainView = mainView;
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {
    if (model.getTrackInfo() == null) {
      trackRepository.getTrackInfo().subscribe(trackInfo -> {
        model.setTrackInfo(trackInfo);
        mainView.updateTrackInfo(trackInfo);
      });
    } else {
      mainView.updateTrackInfo(model.getTrackInfo());
    }

    if (model.getRating() == null) {
      trackRepository.getRating().subscribe(rating -> {
        model.setRating(rating);
      });
    } else {

    }

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
