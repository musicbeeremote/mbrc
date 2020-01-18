package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.withContext

class ConnectivityVerifierImpl(
  private val deserializationAdapter: DeserializationAdapter,
  private val requestManager: RequestManager,
  private val dispatchers: AppCoroutineDispatchers
) : ConnectivityVerifier {

  private fun getMessage(response: String) = deserializationAdapter.objectify(
    response,
    SocketMessage::class
  )

  override suspend fun verify(): Boolean = withContext(dispatchers.network) {
    try {
      val connection = requestManager.openConnection(false)
      val verifyMessage = SocketMessage.create(Protocol.VerifyConnection)
      val response = requestManager.request(connection, verifyMessage)
      connection.close()
      val message = getMessage(response)

      if (Protocol.VerifyConnection == message.context) {
        return@withContext true
      }
    } catch (e: Exception) {
      return@withContext false
    }

    throw NoValidPluginConnection()
  }

  class NoValidPluginConnection : Exception()
}
