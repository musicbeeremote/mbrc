package com.kelsos.mbrc.networking.client

import io.reactivex.Single

interface ConnectivityVerifier {
  fun verify(): Single<Boolean>
}