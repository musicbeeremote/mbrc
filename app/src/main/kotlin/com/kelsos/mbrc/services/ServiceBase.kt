package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.PageRange
import com.kelsos.mbrc.data.ProtocolPayload
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.mappers.InetAddressMapper
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.utilities.SettingsManager
import rx.Emitter.BackpressureMode.LATEST
import rx.Observable
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import javax.inject.Inject

open class ServiceBase {
  @Inject lateinit var mapper: ObjectMapper
  @Inject lateinit var repository: ConnectionRepository
  @Inject lateinit var settingsManager: SettingsManager

  private var socket: Socket? = null

  @Throws(JsonProcessingException::class)
  private fun getMessage(message: SocketMessage): ByteArray {
    return (mapper.writeValueAsString(message) + "\r\n").toByteArray()
  }

  fun request(request: String, data: Any = true): Observable<SocketMessage> {
    return Observable.using<String, Socket>({
      this.getSocket()
    }, {
      this.getObservable(it)
    }, {
      this.cleanup(it)
    }).flatMap { getSocketMessageObservable(request, data, it) }.skipWhile { this.shouldSkip(it) }
  }

  private fun getSocketMessageObservable(request: String, data: Any, s: String): Observable<SocketMessage> {
    return Observable.fromEmitter<SocketMessage>({
      try {
        val message = mapper.readValue(s, SocketMessage::class.java)
        val context = message.context

        if (Protocol.Player == context) {
          val payload = ProtocolPayload(settingsManager.getClientId())
          payload.noBroadcast = true
          payload.protocolVersion = Protocol.ProtocolVersionNumber
          sendMessage(SocketMessage.create(Protocol.ProtocolTag, payload))
        } else if (Protocol.ProtocolTag == context) {
          sendMessage(SocketMessage.create(request, data))
        }

        message.data = mapper.writeValueAsString(message.data)

        it.onNext(message)
        it.onCompleted()
      } catch (e: IOException) {
        it.onError(e)
      }
    }, LATEST)
  }

  private fun shouldSkip(ms: SocketMessage): Boolean {
    return Protocol.Player == ms.context || Protocol.ProtocolTag == ms.context
  }

  @Throws(IOException::class)
  private fun sendMessage(socketMessage: SocketMessage) {
    socket!!.outputStream.write(getMessage(socketMessage))
  }

  private fun cleanup(socket: Socket) {
    Timber.v("Cleaning auxiliary socket")
    if (!socket.isClosed) {
      try {
        socket.close()
      } catch (ex: IOException) {
        Timber.v(ex, "Failed to clause the auxiliary socket")
      }

    }
  }

  private fun getObservable(socket: Socket): Observable<out String> {
    try {
      val `in` = InputStreamReader(socket.inputStream, Const.UTF_8)
      val bufferedReader = BufferedReader(`in`)
      return Observable.create(OnSubscribeReader(bufferedReader))
    } catch (ex: IOException) {
      return Observable.error<String>(ex)
    }

  }

  private fun getSocket(): Socket {
    val mapper = InetAddressMapper()
    val connectionSettings = repository.default
    try {
      if (connectionSettings == null) {
        throw RuntimeException("no settings")
      }
      val socketAddress = mapper.map(connectionSettings)
      Timber.v("Creating new socket")
      socket = Socket()
      socket!!.soTimeout = 40 * 1000
      socket!!.connect(socketAddress)
      sendMessage(SocketMessage.create(Protocol.Player, "Android"))
      return socket!!
    } catch (e: IOException) {
      throw RuntimeException(e)
    }

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
