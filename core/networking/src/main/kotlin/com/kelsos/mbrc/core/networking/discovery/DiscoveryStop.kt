package com.kelsos.mbrc.core.networking.discovery

import com.kelsos.mbrc.core.common.data.ConnectionSettings

sealed class DiscoveryStop {
  object NoWifi : DiscoveryStop()

  object NotFound : DiscoveryStop()

  /**
   * Discovery succeeded and found at least one MusicBee host. [settings]
   * is guaranteed non-empty and de-duplicated by address.
   */
  class Complete(val settings: List<ConnectionSettings>) : DiscoveryStop() {
    init {
      require(settings.isNotEmpty()) { "Complete requires at least one connection" }
    }

    /** First host found, used by single-host callers (e.g. auto-connect). */
    val first: ConnectionSettings get() = settings.first()
  }
}
