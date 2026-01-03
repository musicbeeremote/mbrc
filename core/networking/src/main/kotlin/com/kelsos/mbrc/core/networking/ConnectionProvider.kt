package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.networking.discovery.DiscoveryStop

/**
 * Extended connection provider that includes discovery functionality.
 * Used by ClientConnectionManager to get or discover connections.
 */
interface ConnectionProvider : DefaultConnectionProvider {
  /**
   * Attempts to discover a MusicBee server on the network.
   * @return DiscoveryStop indicating the result of discovery
   */
  suspend fun discover(): DiscoveryStop
}
