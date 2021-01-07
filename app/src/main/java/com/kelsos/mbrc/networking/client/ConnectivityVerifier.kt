package com.kelsos.mbrc.networking.client

import arrow.core.Either

interface ConnectivityVerifier {
  suspend fun verify(): Either<Throwable, Boolean>
}
