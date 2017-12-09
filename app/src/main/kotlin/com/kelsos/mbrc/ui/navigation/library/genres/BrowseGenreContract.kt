package com.kelsos.mbrc.ui.navigation.library.genres

import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseGenrePresenter : Presenter<BrowseGenreView> {
  fun load()
  fun reload()
}

interface BrowseGenreView : BaseView {
  fun update(cursor: List<Genre>)
  fun failure(it: Throwable)
  fun hideLoading()
  fun showLoading()
}
