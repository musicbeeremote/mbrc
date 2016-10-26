package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.navigation.library.tracks.BrowseTrackView

interface BrowseTrackPresenter {
  fun bind(view: BrowseTrackView)

  fun load()

  fun queue(track: Track, @Action action: String)

  fun load(page: Int, totalItemsCount: Int)
}
