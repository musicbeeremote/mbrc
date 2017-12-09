package com.kelsos.mbrc.ui.navigation.library.genres

import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.Queue
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseGenrePresenter : Presenter<BrowseGenreView> {
  fun load()
  fun sync()
  fun queue(@Queue.Action action: String, genre: Genre)
}

interface BrowseGenreView : BaseView {
  fun update(cursor: List<Genre>)
  fun search(term: String)
  fun queue(success: Boolean, tracks: Int)
  fun hideLoading()
  fun showLoading()
}
