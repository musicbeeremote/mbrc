package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.repository.ConnectionRepository
import io.reactivex.Single
import java.io.IOException
import javax.inject.Inject

class ConnectionVerifierImpl
@Inject constructor(
    private val mapper: ObjectMapper,
    val repository: ConnectionRepository
) : ConnectionVerifier, ServiceCaller(mapper, repository) {

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
