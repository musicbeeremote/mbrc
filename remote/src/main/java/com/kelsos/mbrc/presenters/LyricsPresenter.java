package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.LyricsView;

public interface LyricsPresenter {
  void bind(LyricsView view);
  void onPause();
  void onResume();
}
