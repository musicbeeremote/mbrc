package com.kelsos.mbrc.ui.navigation.playlists.tracks

import com.kelsos.mbrc.domain.PlaylistTrack

interface PlaylistTrackView {

  fun showErrorWhileLoading()

  fun update(data: List<PlaylistTrack>)
}
