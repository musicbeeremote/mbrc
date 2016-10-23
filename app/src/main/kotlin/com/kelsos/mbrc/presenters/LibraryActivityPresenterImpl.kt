package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.LibraryActivityView
import javax.inject.Inject

class LibraryActivityPresenterImpl
@Inject constructor() : LibraryActivityPresenter {
  private var view: LibraryActivityView? = null

  override fun bind(view: LibraryActivityView) {

    this.view = view
  }

  override fun onResume() {

  }

  override fun onPause() {

  }

  override fun checkLibrary() {

  }
}
