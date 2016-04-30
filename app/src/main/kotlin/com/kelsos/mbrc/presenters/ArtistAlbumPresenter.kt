package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.ui.views.ArtistAlbumsView

interface ArtistAlbumPresenter {
  fun load(artistId: Long)

  fun bind(view: ArtistAlbumsView)

  fun queue(@Queue.Action action: String, album: Album)
}
