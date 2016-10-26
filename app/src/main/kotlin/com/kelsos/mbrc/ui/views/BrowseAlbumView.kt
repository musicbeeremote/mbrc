package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.mvp.BaseView

interface BrowseAlbumView : BaseView {
  fun updateData(data: List<Album>)

  fun clearData()
}
