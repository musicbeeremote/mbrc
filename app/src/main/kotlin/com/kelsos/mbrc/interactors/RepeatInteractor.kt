package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.Repeat.Mode
import rx.Observable

interface RepeatInteractor {
  fun getRepeat(): Observable<String>
  fun setRepeat(@Mode mode: String): Observable<String>
}
