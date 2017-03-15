package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.RadioStation
import rx.Observable

interface RadioService {
  fun getRadios(offset: Int, limit: Int): Observable<Page<RadioStation>>
}
