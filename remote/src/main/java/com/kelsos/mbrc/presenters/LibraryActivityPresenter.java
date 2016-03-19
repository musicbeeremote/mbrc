package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.LibraryActivityView;

public interface LibraryActivityPresenter {
  void bind(LibraryActivityView view);

  void onResume();

  void onPause();

  void checkLibrary();
}
