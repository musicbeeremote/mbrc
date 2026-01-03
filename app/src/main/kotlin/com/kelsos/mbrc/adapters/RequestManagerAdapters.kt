package com.kelsos.mbrc.adapters

import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.networking.ClientIdProvider
import com.kelsos.mbrc.core.networking.ConnectionProvider
import com.kelsos.mbrc.core.networking.DefaultConnectionProvider
import com.kelsos.mbrc.core.networking.LibrarySyncTrigger
import com.kelsos.mbrc.core.networking.ProtocolActionFactory
import com.kelsos.mbrc.core.networking.discovery.DiscoveryStop
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolAction
import com.kelsos.mbrc.core.networking.protocol.usecases.CommandFactory
import com.kelsos.mbrc.feature.library.domain.LibrarySyncWorkHandler
import com.kelsos.mbrc.feature.settings.data.ClientInformationStore
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository

/**
 * Adapter that bridges [ClientInformationStore] to [ClientIdProvider].
 */
class ClientIdProviderAdapter(private val clientInformationStore: ClientInformationStore) :
  ClientIdProvider {
  override suspend fun getClientId(): String = clientInformationStore.getClientId()
}

/**
 * Adapter that bridges [ConnectionRepository] to [DefaultConnectionProvider].
 */
class DefaultConnectionProviderAdapter(private val connectionRepository: ConnectionRepository) :
  DefaultConnectionProvider {
  override fun getDefault(): ConnectionSettings? = connectionRepository.getDefault()
}

/**
 * Adapter that bridges [ConnectionRepository] to [ConnectionProvider].
 * Provides both default connection and discovery functionality.
 */
class ConnectionProviderAdapter(private val connectionRepository: ConnectionRepository) :
  ConnectionProvider {
  override fun getDefault(): ConnectionSettings? = connectionRepository.getDefault()

  override suspend fun discover(): DiscoveryStop = connectionRepository.discover()
}

/**
 * Adapter that bridges [LibrarySyncWorkHandler] to [LibrarySyncTrigger].
 */
class LibrarySyncTriggerAdapter(private val librarySyncWorkHandler: LibrarySyncWorkHandler) :
  LibrarySyncTrigger {
  override fun sync(auto: Boolean) {
    librarySyncWorkHandler.sync(auto)
  }
}

/**
 * Adapter that bridges [CommandFactory] to [ProtocolActionFactory].
 */
class ProtocolActionFactoryAdapter(private val commandFactory: CommandFactory) :
  ProtocolActionFactory {
  override fun create(protocol: Protocol): ProtocolAction = commandFactory.create(protocol)
}
