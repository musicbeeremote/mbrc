package com.kelsos.mbrc.networking.client

import arrow.core.Either
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol

class ConnectivityVerifierImpl(
  private val deserializationAdapter: DeserializationAdapter,
  private val requestManager: RequestManager,
  val repository: ConnectionRepository
) : ConnectivityVerifier {

  override suspend fun verify(): Either<Throwable, Boolean> {
    return Either.catch {
      val connection = requestManager.openConnection(false)
      val response = requestManager.request(
        connection,
        SocketMessage.create(Protocol.VerifyConnection)
      )
      connection.close()
      val (context, data) = deserializationAdapter.objectify(response, SocketMessage::class)

      if (context != Protocol.VerifyConnection) {
        throw NoValidPluginConnection()
      }
      true
    }
  }

  class NoValidPluginConnection : Exception()
}