package com.kelsos.mbrc.networking.client

import arrow.core.Either
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.withContext
import timber.log.Timber

class ConnectivityVerifierImpl(
  private val deserializationAdapter: DeserializationAdapter,
  private val requestManager: RequestManager,
  private val dispatchers: AppCoroutineDispatchers
) : ConnectivityVerifier {

  override suspend fun verify(): Either<Throwable, Boolean> = Either.catch {
    withContext(dispatchers.network) {
      val connection = requestManager.openConnection(false)
      val verifyMessage = SocketMessage.create(Protocol.VerifyConnection)
      val response = requestManager.request(connection, verifyMessage)
      connection.close()
      val (context, _) = deserializationAdapter.objectify(
        response,
        SocketMessage::class
      )

      val type = Protocol.fromString(context)

      if (type != Protocol.VerifyConnection) {
        throw NoValidPluginConnection()
      }
      return@withContext true
    }
  }.mapLeft {
    Timber.v(it)
    if (it is NoValidPluginConnection) it else NoValidPluginConnection(it)
  }

  class NoValidPluginConnection(cause: Throwable? = null) : Exception(cause)
}
