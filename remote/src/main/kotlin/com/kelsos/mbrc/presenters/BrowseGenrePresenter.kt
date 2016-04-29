package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.ui.views.BrowseGenreView

interface BrowseGenrePresenter : PlaylistAdder {
  fun bind(view: BrowseGenreView)

  fun load()

  fun queue(genre: Genre, @Queue.Action action: String)

  fun load(page: Int)
}
