package com.kelsos.mbrc.presenters.interfaces;

import com.kelsos.mbrc.ui.views.MainView;

public interface IMainViewPresenter {
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
