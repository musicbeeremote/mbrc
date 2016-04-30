package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Track

interface BrowseTrackView {

  fun clearData()

  fun appendPage(tracks: List<Track>)
}
