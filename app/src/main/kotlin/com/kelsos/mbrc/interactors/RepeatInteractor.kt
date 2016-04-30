package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.Repeat

import rx.Observable

interface RepeatInteractor {
    fun getRepeat(): Observable<String>
    fun setRepeat(@Repeat.Mode mode: String): Observable<String>
}
