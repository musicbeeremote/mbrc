package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mvp.IView

interface BrowseAlbumView : IView {
  fun updateData(data: List<Album>)

  fun clearData()
}
