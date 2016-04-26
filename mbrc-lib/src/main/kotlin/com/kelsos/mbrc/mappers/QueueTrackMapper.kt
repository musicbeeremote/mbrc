package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.dto.NowPlayingTrack
import rx.Observable
import rx.functions.Func1

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
        return Observable.from(list).map<QueueTrack>(Func1<NowPlayingTrack, QueueTrack> { map(it) }).toList().toBlocking().first()
    }
}//no instance
