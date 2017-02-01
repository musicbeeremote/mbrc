package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.RadioStation
import rx.Single

interface RadioService {
  fun getRadios(): Single<List<RadioStation>>
}
