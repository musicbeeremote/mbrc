package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.DeserializationAdapter
import com.kelsos.mbrc.networking.ActiveConnection
import com.kelsos.mbrc.networking.RequestManager
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single
import java.net.SocketTimeoutException
import javax.inject.Inject

class ConnectivityVerifierImpl
@Inject constructor(
  private val deserializationAdapter: DeserializationAdapter,
  private val requestManager: RequestManager,
  val repository: ConnectionRepository
) : ConnectivityVerifier {

  private fun openConnection(): Single<ActiveConnection> = Single.create<ActiveConnection> {
    try {
      it.onSuccess(requestManager.openConnection(false))
    } catch (ex: SocketTimeoutException) {
      it.tryOnError(ex)
    }
  }

  override fun verify(): Single<Boolean> {
    return openConnection().flatMap {
      requestManager.request(it, SocketMessage.create(Protocol.VerifyConnection)).map {
        deserializationAdapter.objectify(it, SocketMessage::class)
      }.map {
        if (it.context == Protocol.VerifyConnection) {
          return@map true
        }
        throw NoValidPluginConnection()
      }
        .doFinally { it.close() }
    }
  }

  class NoValidPluginConnection : Exception()
}