package com.kelsos.mbrc.ui.navigation.library.artists

import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface BrowseArtistView : BaseView {
  fun update(pagedList: PagedList<ArtistEntity>)
  fun failure(throwable: Throwable)
  fun hideLoading()
  fun updateIndexes(indexes: List<String>)
}

interface BrowseArtistPresenter : Presenter<BrowseArtistView> {
  fun load()
  fun reload()
}