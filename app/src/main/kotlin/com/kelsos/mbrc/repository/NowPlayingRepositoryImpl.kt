package com.kelsos.mbrc.repository

import javax.inject.Inject
import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.dto.NowPlayingTrack
import com.kelsos.mbrc.dto.PageResponse
import com.kelsos.mbrc.mappers.QueueTrackMapper
import com.kelsos.mbrc.services.api.NowPlayingService
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

class NowPlayingRepositoryImpl : NowPlayingRepository {
    @Inject private lateinit var service: NowPlayingService

    override fun getNowPlayingList(): Observable<List<QueueTrack>> = Observable.range(0, Integer.MAX_VALUE - 1)
            .concatMap<PageResponse<NowPlayingTrack>>(Func1 {
                service.getNowPlayingList(LIMIT * it, LIMIT)
                        .subscribeOn(Schedulers.io())
            })
            .takeWhile { page -> page.offset < page.total }
            .flatMap({
                Observable.just<List<QueueTrack>>(QueueTrackMapper.map(it.data))
            })

    companion object {

        val LIMIT = 400
    }
}
