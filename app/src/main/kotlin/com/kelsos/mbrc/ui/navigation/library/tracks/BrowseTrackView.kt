package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.domain.Track

interface BrowseTrackView {

  fun clearData()

  fun appendPage(tracks: List<Track>)
}
