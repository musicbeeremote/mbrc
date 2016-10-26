package com.kelsos.mbrc.ui.navigation.library.artists

import com.kelsos.mbrc.domain.Artist

interface BrowseArtistView {
  fun showEnqueueSuccess()

  fun showEnqueueFailure()

  fun load(artists: List<Artist>)

  fun clear()
}
