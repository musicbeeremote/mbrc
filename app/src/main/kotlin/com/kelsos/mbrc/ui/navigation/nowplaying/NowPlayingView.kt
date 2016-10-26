package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.domain.QueueTrack

interface NowPlayingView {
  fun updatePlayingTrack(track: QueueTrack)

  fun updateAdapter(data: List<QueueTrack>)
}
