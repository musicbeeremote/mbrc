package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.interactors.LibraryStatusInteractor;
import com.kelsos.mbrc.ui.views.LibraryActivityView;

public class LibraryActivityPresenterImpl implements LibraryActivityPresenter {
  private LibraryActivityView view;

  @Inject private LibraryStatusInteractor libraryStatusInteractor;

  @Override public void bind(LibraryActivityView view) {

    this.view = view;
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }

  @Override public void checkLibrary() {

  }
}
