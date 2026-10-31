package com.kelsos.mbrc.feature.settings.domain

import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.ClientConnectionUseCase
import com.kelsos.mbrc.core.networking.UserConnectPreparer
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/**
 * The one way a user moves the app to another MusicBee host: the connection manager's "set as
 * default" and a scanned connect link both go through here, so anything that has to happen on a
 * change of host (such as resyncing the library, #398) is added in one place.
 *
 * A switch runs to completion once started. Stopping after the default is stored but before the
 * reconnect would leave the app talking to the old host while the new one is the default.
 */
interface DefaultConnectionSwitcher {
  suspend fun switchTo(connection: ConnectionSettings)

  /**
   * Switches to the host at [address] and [port]. It is stored as [ConnectionRepository.saveAsDefault]
   * describes: reused, moved from the entry [sameHostId], or added under [name]. [onSaved] receives
   * the stored host before the reconnect starts.
   */
  suspend fun switchTo(
    address: String,
    port: Int,
    name: String,
    sameHostId: Long?,
    onSaved: suspend (ConnectionSettings) -> Unit
  )
}

class DefaultConnectionSwitcherImpl(
  private val repository: ConnectionRepository,
  private val clientConnectionUseCase: ClientConnectionUseCase,
  private val userConnectPreparer: UserConnectPreparer,
  private val dispatchers: AppCoroutineDispatchers
) : DefaultConnectionSwitcher {
  override suspend fun switchTo(connection: ConnectionSettings) = withContext(NonCancellable) {
    withContext(dispatchers.database) { repository.setDefault(connection) }
    reconnect()
  }

  override suspend fun switchTo(
    address: String,
    port: Int,
    name: String,
    sameHostId: Long?,
    onSaved: suspend (ConnectionSettings) -> Unit
  ) = withContext(NonCancellable) {
    onSaved(repository.saveAsDefault(address, port, name, sameHostId))
    reconnect()
  }

  private suspend fun reconnect() {
    userConnectPreparer.prepare()
    clientConnectionUseCase.connect(reset = true)
  }
}
