package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.controller.PlayerController;
import com.kelsos.mbrc.controller.TrackController;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.ui.views.MainView;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import roboguice.inject.ContextSingleton;

@ContextSingleton
public class MainViewPresenter implements IMainViewPresenter {
  private MainView mainView;
  private final ScheduledExecutorService progressScheduler = Executors.newScheduledThreadPool(1);
  private ScheduledFuture progressUpdateHandler;
  @Inject private PlayerController playerController;
  @Inject private TrackController trackController;

  @Override public void bind(MainView mainView) {
    this.mainView = mainView;
  }

  private void stopTrackAnimation() {
    if (progressUpdateHandler != null) {
      progressUpdateHandler.cancel(true);
    }
  }


  private void animateTrackProgress() {

    /* If the scheduled tasks is not null then cancel it and clear it along with the
    timer to create them anew */
    final int timePeriod = 1;
    PlayState state = playerController.getPlayState();
    if (state == PlayState.PAUSED || state == PlayState.STOPPED) {
      return;
    }

    final Runnable updateProgress = () -> {

      int currentProgress = mainView.getCurrentProgress() / 1000;
      final int currentMinutes = currentProgress / 60;
      final int currentSeconds = currentProgress % 60;

      mainView.updateProgress(currentProgress + 1000, currentMinutes, currentSeconds);

    };

    progressUpdateHandler =
        progressScheduler.scheduleAtFixedRate(updateProgress, 0, timePeriod, TimeUnit.SECONDS);
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {
    mainView.updateCover(trackController.getCover());
    mainView.updateTrackInfo(trackController.getTrackInfo());
    mainView.updateShuffle(playerController.getShuffleState());
  }

  @Override public void onPlayPausePressed() {
    playerController.onPlayPressed();
  }

  @Override public void onPreviousPressed() {
    playerController.onPreviousPressed();
  }

  @Override public void onNextPressed() {
    playerController.onNextPressed();
  }

  @Override public void onStopPressed() {
    playerController.onStopPressed();
  }

  @Override public void onMutePressed() {
    playerController.onMutePressed();
  }

  @Override public void onShufflePressed() {
    playerController.onShufflePressed();
  }

  @Override public void onRepeatPressed() {
    playerController.onRepeatPressed();
  }

  @Override public void onVolumeChange(int volume) {

  }

  @Override public void onPositionChange(int position) {
    trackController.changePosition(position).subscribe(trackPositionResponse -> {

    });
  }

  @Override public void onScrobbleToggle() {

  }

  @Override public void onLfmLoveToggle() {

  }

  private void positionUpdate() {
    final int total = 0;
    final int current = 0;
    int currentSeconds = current / 1000;
    int totalSeconds = total / 1000;

    final int currentMinutes = currentSeconds / 60;
    final int totalMinutes = totalSeconds / 60;

    currentSeconds %= 60;
    totalSeconds %= 60;

    mainView.updateDuration(total, totalMinutes, totalSeconds);
    mainView.updateProgress(current, currentMinutes, currentSeconds);
  }
}
