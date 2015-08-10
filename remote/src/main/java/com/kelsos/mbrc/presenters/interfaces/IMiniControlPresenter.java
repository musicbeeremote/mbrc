package com.kelsos.mbrc.presenters.interfaces;

import com.kelsos.mbrc.ui.views.MiniControlView;

public interface IMiniControlPresenter {
  void onNextPressed();
  void onPreviousPressed();
  void onPlayPause();
  void bind(MiniControlView view);
  void onResume();
  void onPause();
}
