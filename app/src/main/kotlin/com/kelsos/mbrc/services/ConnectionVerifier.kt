package com.kelsos.mbrc.services

import rx.Single

interface ConnectionVerifier {
  fun verify(): Single<Boolean>
}
