package com.kelsos.mbrc.core.networking.client

import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.RequestManager
import com.kelsos.mbrc.core.networking.data.DeserializationAdapter
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import kotlinx.coroutines.withContext
import timber.log.Timber

fun interface ConnectivityVerifier {
  suspend fun verify(): Boolean
}

class ConnectivityVerifierImpl(
  private val deserializationAdapter: DeserializationAdapter,
  private val requestManager: RequestManager,
  private val dispatchers: AppCoroutineDispatchers
) : ConnectivityVerifier {
  private fun getMessage(response: String) = deserializationAdapter.objectify(
    response,
    SocketMessage::class
  )

  override suspend fun verify(): Boolean = withContext(dispatchers.io) {
    runCatching {
      val connection = requestManager.openConnection(false)
      connection.use { connection ->
        val response = requestManager.request(
          connection,
          SocketMessage.create(Protocol.VerifyConnection)
        )
        val message = getMessage(response)
        Protocol.VERIFY_CONNECTION == message.context
      }
    }.onFailure { e ->
      Timber.e(e, "Connection verification failed")
    }.getOrDefault(false)
  }
}
