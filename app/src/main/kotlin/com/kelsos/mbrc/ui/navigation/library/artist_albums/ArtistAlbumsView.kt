package com.kelsos.mbrc.ui.navigation.library.artist_albums

import com.kelsos.mbrc.domain.Album

interface ArtistAlbumsView {

  fun update(data: List<Album>)

  fun showLoadFailed()

  fun queueSuccess()

  fun queueFailed()
}
