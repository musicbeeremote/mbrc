package com.kelsos.mbrc.services

import rx.Single

interface CoverService {

  fun getCover(): Single<String>

}
