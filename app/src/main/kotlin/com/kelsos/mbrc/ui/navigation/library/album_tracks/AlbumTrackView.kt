package com.kelsos.mbrc.ui.navigation.library.album_tracks

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.domain.Track

interface AlbumTrackView {
  fun updateAlbum(album: Album)

  fun updateTracks(tracks: List<Track>)

  fun showPlaySuccess()

  fun showPlayFailed()

  fun showTrackSuccess()

  fun showTrackFailed()
}
