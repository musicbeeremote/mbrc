package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.Sorting.Fields
import com.kelsos.mbrc.content.library.albums.Sorting.Order
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface BrowseAlbumView : BaseView {
  fun update(cursor: FlowCursorList<Album>)
  fun failure(throwable: Throwable)
  fun hideLoading()
  fun showLoading()
  fun showSorting(@Order order: Long, @Fields selection: Long)
}

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {
  fun load()
  fun reload()
  fun showSorting()
  fun order(@Order order: Long)
  fun sortBy(@Fields selection: Long)
}

