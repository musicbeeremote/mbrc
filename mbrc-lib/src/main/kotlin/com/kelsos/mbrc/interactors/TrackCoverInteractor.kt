package com.kelsos.mbrc.interactors

import android.graphics.Bitmap

import rx.Observable

interface TrackCoverInteractor {
    fun execute(b: Boolean): Observable<Bitmap?>
}
