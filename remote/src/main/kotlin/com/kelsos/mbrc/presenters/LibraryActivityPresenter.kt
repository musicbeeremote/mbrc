package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.ui.views.LibraryActivityView

interface LibraryActivityPresenter {
  fun bind(view: LibraryActivityView)

  fun onResume()

  fun onPause()

  fun checkLibrary()
}
