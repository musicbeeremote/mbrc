package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.ui.views.BrowseAlbumView

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {

  fun queue(album: Album, @Action action: String)

  fun load(page: Int = 0)
}
