package com.kelsos.mbrc.networking.discovery

import arrow.core.Either
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity

interface RemoteServiceDiscovery {
  suspend fun discover(): Either<DiscoveryStop, ConnectionSettingsEntity>
}