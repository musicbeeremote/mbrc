package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mvp.IPresenter
import com.kelsos.mbrc.ui.views.BrowseAlbumView

interface BrowseAlbumPresenter : IPresenter<BrowseAlbumView> {

  fun queue(album: Album, @Action action: String)

  fun load(page: Int = 0)
}
