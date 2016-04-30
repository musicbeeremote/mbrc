package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.TrackPosition

import rx.Observable

interface TrackPositionInteractor {
    fun getPosition(): Observable<TrackPosition>
    fun setPosition(position: Int): Observable<TrackPosition>
}
