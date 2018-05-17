package com.kelsos.mbrc.ui.navigation.library.albums

import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.Sorting.Fields
import com.kelsos.mbrc.content.library.albums.Sorting.Order
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseAlbumView : BaseView {
  fun update(pagedList: PagedList<AlbumEntity>)

  fun failure(throwable: Throwable)

  fun hideLoading()

  fun showSorting(@Order order: Int, @Fields selection: Int)

  fun updateIndexes(indexes: List<String>)
}

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {
  fun load()
  fun reload()
  fun showSorting()
  fun order(@Order order: Int)
  fun sortBy(@Fields selection: Int)
}