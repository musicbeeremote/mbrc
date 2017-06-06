package com.kelsos.mbrc.networking

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single
import java.io.IOException
import javax.inject.Inject

class ConnectivityVerifierImpl
@Inject constructor(
    private val mapper: ObjectMapper,
    val repository: ConnectionRepository
) : ConnectivityVerifier, ApiRequestBase(mapper, repository) {

  override fun verify(): Single<Boolean> {
    return call(SocketMessage.create(Protocol.VerifyConnection))
        .firstOrError()
        .flatMap { checkIfSuccess(it) }
  }

  private fun checkIfSuccess(serviceMessage: ServiceMessage): Single<Boolean> {
    return Single.create {
      try {
        val message = mapper.readValue(serviceMessage.message, SocketMessage::class.java)
        if (Protocol.VerifyConnection == message.context) {
          it.onSuccess(true)
        } else {
          it.onError(NoValidPluginConnection())
        }
      } catch (e: IOException) {
        it.onError(e)
      }
    }
  }

  class NoValidPluginConnection : Exception()
}
