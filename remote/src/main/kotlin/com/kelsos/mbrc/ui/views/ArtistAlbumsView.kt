package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Album

interface ArtistAlbumsView {

  fun update(data: List<Album>)

  fun showLoadFailed()

  fun queueSuccess()

  fun queueFailed()
}
