package com.kelsos.mbrc.services

import io.reactivex.Single

interface CoverService {

  fun getCover(): Single<String>

}
