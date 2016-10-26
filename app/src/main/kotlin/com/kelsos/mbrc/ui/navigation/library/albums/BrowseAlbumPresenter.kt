package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.ui.navigation.library.albums.BrowseAlbumView

interface BrowseAlbumPresenter : Presenter<BrowseAlbumView> {

  fun queue(album: Album, @Action action: String)

  fun load(page: Int = 0)
}
