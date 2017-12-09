package com.kelsos.mbrc.ui.navigation.library.artistalbums

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface ArtistAlbumsView : BaseView {
  fun update(albums: List<Album>)
}

interface ArtistAlbumsPresenter: Presenter<ArtistAlbumsView> {
  fun load(artist: String)
}

