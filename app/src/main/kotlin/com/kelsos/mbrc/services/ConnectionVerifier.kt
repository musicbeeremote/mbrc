package com.kelsos.mbrc.services

import io.reactivex.Single

interface ConnectionVerifier {
  fun verify(): Single<Boolean>
}
