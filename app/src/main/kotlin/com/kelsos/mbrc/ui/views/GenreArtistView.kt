package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Artist

interface GenreArtistView {
  fun update(data: List<Artist>)

  fun onQueueSuccess()

  fun onQueueFailure()
}
