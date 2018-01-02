package com.kelsos.mbrc.ui.navigation.library.artistalbums

import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface ArtistAlbumsView : BaseView {
  fun update(albums: List<AlbumEntity>)
}

interface ArtistAlbumsPresenter: Presenter<ArtistAlbumsView> {
  fun load(artist: String)
}

