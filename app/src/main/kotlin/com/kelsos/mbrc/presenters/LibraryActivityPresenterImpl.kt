package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.interactors.LibrarySyncInteractor
import com.kelsos.mbrc.ui.views.LibraryActivityView

class LibraryActivityPresenterImpl : LibraryActivityPresenter {
  private var view: LibraryActivityView? = null

  @Inject private lateinit var librarySyncInteractor: LibrarySyncInteractor

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
