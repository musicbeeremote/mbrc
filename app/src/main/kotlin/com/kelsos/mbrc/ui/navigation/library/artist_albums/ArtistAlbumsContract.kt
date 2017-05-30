package com.kelsos.mbrc.ui.navigation.library.artist_albums

import com.kelsos.mbrc.library.albums.Album
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface ArtistAlbumsView : BaseView {
  fun update(albums: FlowCursorList<Album>)
}

interface ArtistAlbumsPresenter: Presenter<ArtistAlbumsView> {
  fun load(artist: String)
}

