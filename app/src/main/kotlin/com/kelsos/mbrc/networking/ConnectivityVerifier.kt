package com.kelsos.mbrc.networking

interface ConnectivityVerifier {
  suspend fun verify(): Boolean
}
