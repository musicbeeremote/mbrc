package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.QueueTrack

interface NowPlayingView {
  fun updatePlayingTrack(track: QueueTrack)

  fun updateAdapter(data: List<QueueTrack>)
}
