package com.kelsos.mbrc.networking

import io.reactivex.Single

interface ConnectivityVerifier {
  fun verify(): Single<Boolean>
}
