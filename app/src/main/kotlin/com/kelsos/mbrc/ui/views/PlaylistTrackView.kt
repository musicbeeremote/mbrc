package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.PlaylistTrack

interface PlaylistTrackView {

  fun showErrorWhileLoading()

  fun update(data: List<PlaylistTrack>)
}
