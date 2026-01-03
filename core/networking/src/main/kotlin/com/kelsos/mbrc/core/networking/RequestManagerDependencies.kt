package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.common.data.ConnectionSettings

/**
 * Provides client identification for the protocol handshake.
 */
fun interface ClientIdProvider {
  suspend fun getClientId(): String
}

/**
 * Provides the default connection settings.
 */
fun interface DefaultConnectionProvider {
  fun getDefault(): ConnectionSettings?
}
