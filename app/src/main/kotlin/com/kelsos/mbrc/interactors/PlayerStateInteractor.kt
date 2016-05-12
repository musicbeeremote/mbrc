package com.kelsos.mbrc.interactors

import rx.Observable

interface PlayerStateInteractor {
  fun getState(): Observable<String>
}
