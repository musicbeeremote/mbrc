package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface LibraryView : BaseView

interface LibraryPresenter : Presenter<LibraryView> {
  fun checkLibrary()
}
