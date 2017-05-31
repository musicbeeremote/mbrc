package com.kelsos.mbrc.networking

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.PageRange
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.preferences.SettingsManager
import io.reactivex.Observable
import timber.log.Timber
import java.io.IOException

open class ApiBase(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    private val settingsManager: SettingsManager
) : ApiRequestBase(mapper, repository) {

  fun request(request: String, data: Any? = null): Observable<SocketMessage> {
    return call().flatMap { getSocketMessageObservable(request, data ?: "", it) }
        .skipWhile { it.shouldSkip() }
  }

  private fun getSocketMessageObservable(
      request: String,
      data: Any,
      serviceMessage: ServiceMessage): Observable<SocketMessage> {
    return Observable.create<SocketMessage> {
      try {
        val socket = serviceMessage.socket
        val jsonString = serviceMessage.message
        val message = mapper.readValue(jsonString, SocketMessage::class.java)
        val context = message.context

        Timber.v("$context message received")
        if (Protocol.Player == context) {
          val payload = getProtocolPayload()
          socket.send(SocketMessage.create(Protocol.ProtocolTag, payload))
        } else if (Protocol.ProtocolTag == context) {
          socket.send(SocketMessage.create(request, data))
        }

        message.data = mapper.writeValueAsString(message.data)

        it.onNext(message)
        it.onComplete()
      } catch (e: IOException) {
        it.onError(e)
      }
    }
  }

  private fun getProtocolPayload(): ProtocolPayload {
    val payload = ProtocolPayload(settingsManager.getClientId())
    payload.noBroadcast = true
    payload.protocolVersion = Protocol.ProtocolVersionNumber
    return payload
  }

  private fun SocketMessage.shouldSkip(): Boolean {
    return Protocol.Player == this.context || Protocol.ProtocolTag == this.context
  }

  fun getPageRange(offset: Int, limit: Int): PageRange? {
    var range: PageRange? = null

    if (limit > 0) {
      range = PageRange()
      range.offset = offset
      range.limit = limit
    }
    return range
  }
}
