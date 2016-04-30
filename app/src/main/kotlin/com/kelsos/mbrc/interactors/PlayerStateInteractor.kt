package com.kelsos.mbrc.interactors

import rx.Observable

interface PlayerStateInteractor {
    val state: Observable<String>
}
