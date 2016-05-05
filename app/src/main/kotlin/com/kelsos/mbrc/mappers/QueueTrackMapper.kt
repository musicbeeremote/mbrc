package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.dto.NowPlayingTrack

object QueueTrackMapper {

  fun map(track: NowPlayingTrack): QueueTrack {
    val queueTrack = QueueTrack()
    queueTrack.position = track.position
    queueTrack.path = track.path
    queueTrack.artist = track.artist
    queueTrack.title = track.title
    return queueTrack
  }

  fun map(list: List<NowPlayingTrack>): List<QueueTrack> {
    return list.map { map(it) }.toList()
  }
}
