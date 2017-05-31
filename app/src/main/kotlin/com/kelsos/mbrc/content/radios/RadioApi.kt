package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.networking.protocol.Page
import io.reactivex.Observable

interface RadioApi {
  fun getRadios(offset: Int, limit: Int): Observable<Page<RadioStation>>
}
