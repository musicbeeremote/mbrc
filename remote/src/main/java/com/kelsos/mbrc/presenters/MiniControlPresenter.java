package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.MiniControlView;

public interface MiniControlPresenter {
  void onNextPressed();
  void onPreviousPressed();
  void onPlayPause();
  void bind(MiniControlView view);
  void onResume();
  void onPause();
  void load();
}
