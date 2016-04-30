package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.ui.views.GenreArtistView

interface GenreArtistsPresenter {
  fun bind(view: GenreArtistView)

  fun onPause()

  fun onResume()

  fun load(genreId: Long)

  fun queue(@Queue.Action action: String, artist: Artist)
}
