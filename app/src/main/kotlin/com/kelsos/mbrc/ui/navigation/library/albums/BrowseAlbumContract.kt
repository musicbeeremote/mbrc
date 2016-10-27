package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface BrowseAlbumView : BaseView {
  fun update(cursor: FlowCursorList<Album>)
}

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {
  fun load()
}

