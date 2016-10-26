package com.kelsos.mbrc.ui.navigation.library.genre_artists

import com.kelsos.mbrc.domain.Artist

interface GenreArtistView {
  fun update(data: List<Artist>)

  fun onQueueSuccess()

  fun onQueueFailure()
}
