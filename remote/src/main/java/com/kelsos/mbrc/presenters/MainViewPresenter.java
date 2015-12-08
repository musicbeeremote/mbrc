package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.MainView;

public interface MainViewPresenter {
  void bind(MainView mainView);

  void onPause();

  void onResume();

  void onPlayPausePressed();

  void onPreviousPressed();

  void onNextPressed();

  void onStopPressed();

  void onMutePressed();

  void onShufflePressed();

  void onRepeatPressed();

  void onVolumeChange(int volume);

  void onPositionChange(int position);

  void onScrobbleToggle();

  void onLfmLoveToggle();
}
