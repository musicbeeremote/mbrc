package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.ui.views.BrowseAlbumView

interface BrowseAlbumPresenter {
  fun bind(view: BrowseAlbumView)

  fun queue(album: Album, @Queue.Action action: String)

  fun load()

  fun load(page: Int)
}
