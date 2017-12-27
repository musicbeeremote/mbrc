package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.paging.PagingData
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseGenrePresenter : Presenter<BrowseGenreView> {
  fun load()
  fun sync()
  fun queue(@LibraryPopup.Action action: String, genre: Genre)
}

interface BrowseGenreView : BaseView {
  suspend fun update(genres: PagingData<Genre>)
  fun search(term: String)
  fun queue(success: Boolean, tracks: Int)
  fun hideLoading()
}
