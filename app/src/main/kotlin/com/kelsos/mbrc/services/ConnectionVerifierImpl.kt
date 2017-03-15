package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.mappers.InetAddressMapper
import com.kelsos.mbrc.repository.ConnectionRepository
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import javax.inject.Inject

class ConnectionVerifierImpl
@Inject constructor(
    private val mapper: ObjectMapper,
    private val repository: ConnectionRepository
) : ConnectionVerifier {

  private lateinit var socket: Socket

  override fun verify(): Single<Boolean> {
    return Observable.using<String, Socket>({
      this.getSocket()
    }, {
      this.getObservable(it)
    }, {
      this.cleanup(it)
    }).firstOrError().flatMap { checkIfSuccess(it) }
  }

  @Throws(JsonProcessingException::class)
  private fun getMessage(message: SocketMessage): ByteArray {
    return (mapper.writeValueAsString(message) + "\r\n").toByteArray()
  }

  private fun checkIfSuccess(s: String): Single<Boolean> {
    return Single.create {
      try {
        val message = mapper.readValue(s, SocketMessage::class.java)
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

  @Throws(IOException::class)
  private fun sendMessage(socketMessage: SocketMessage) {
    socket.outputStream.write(getMessage(socketMessage))
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
      socket.soTimeout = 40 * 1000
      socket.connect(socketAddress)
      sendMessage(SocketMessage.create(Protocol.VerifyConnection))
      return socket
    } catch (e: IOException) {
      throw RuntimeException(e)
    }
  }

  class NoValidPluginConnection : Exception()
}
