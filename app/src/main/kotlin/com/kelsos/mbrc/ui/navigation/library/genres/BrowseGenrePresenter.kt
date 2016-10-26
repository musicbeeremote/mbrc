package com.kelsos.mbrc.ui.navigation.library.genres

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.ui.navigation.playlists.dialog.PlaylistAdder
import com.kelsos.mbrc.ui.navigation.library.genres.BrowseGenreView

interface BrowseGenrePresenter : PlaylistAdder {
  fun bind(view: BrowseGenreView)

  fun load()

  fun queue(genre: Genre, @Queue.Action action: String)

  fun load(page: Int)
}
