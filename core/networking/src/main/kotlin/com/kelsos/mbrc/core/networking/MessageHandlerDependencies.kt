package com.kelsos.mbrc.core.networking

import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolAction

/**
 * Interface for triggering library synchronization.
 * Called after successful connection to sync library data.
 */
interface LibrarySyncTrigger {
  fun sync(auto: Boolean)
}

/**
 * Factory interface for creating protocol action handlers.
 * Maps protocol types to their action implementations.
 */
interface ProtocolActionFactory {
  fun create(protocol: Protocol): ProtocolAction
}
