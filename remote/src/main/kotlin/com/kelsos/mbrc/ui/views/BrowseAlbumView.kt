package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Album

interface BrowseAlbumView {
  fun updateData(data: List<Album>)

  fun clearData()
}
