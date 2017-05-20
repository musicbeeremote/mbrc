package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.mappers.InetAddressMapper
import com.kelsos.mbrc.repository.ConnectionRepository
import io.reactivex.Observable
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

open class ServiceCaller(
    private val mapper: ObjectMapper,
    private val repository: ConnectionRepository
) {

  internal fun call(
      firstMessage: SocketMessage = SocketMessage.create(Protocol.Player, "Android")
  ): Observable<ServiceMessage> {
    return Observable.using<ServiceMessage, Socket>({
      this.connect(firstMessage)
    }, {
      this.responses(it)
    }, {
      it.cleanup()
    })
  }

  private fun responses(socket: Socket): Observable<out ServiceMessage> {
    try {
      val `in` = InputStreamReader(socket.inputStream, Const.UTF_8)
      val bufferedReader = BufferedReader(`in`)
      return Observable.create(OnSubscribeReader(bufferedReader, socket))
    } catch (ex: IOException) {
      return Observable.error<ServiceMessage>(ex)
    }
  }

  /**
   * Connects to the default connection and returns the connected [Socket]
   *
   * @param firstMessage the first message that will be send through the socket.
   * Under a normal flow when requesting data this should be a Player Message
   *
   * @return The socket connection where we need in order to perform the service actions.
   */
  private fun connect(firstMessage: SocketMessage): Socket {
    val mapper = InetAddressMapper()
    val connectionSettings = repository.default ?: throw RuntimeException("no settings")
    try {
      val socketAddress = mapper.map(connectionSettings)
      Timber.v("Creating new socket")

      val socket = Socket().apply {
        soTimeout = 20 * 1000
        connect(socketAddress)
        send(firstMessage)
      }

      return socket
    } catch (e: IOException) {
      Timber.v("failed to create socket")
      throw RuntimeException(e)
    }

  }

  /**
   * Converts the [SocketMessage] to the byte array representation of its json format
   * that is followed by an empty line.
   */
  @Throws(JsonProcessingException::class)
  private fun SocketMessage.getBytes(): ByteArray {
    return (mapper.writeValueAsString(this) + "\r\n").toByteArray()
  }

  /**
   * Sends a socket message to the other point of the Socket.
   *
   * @param socketMessage A message that will be send through the connected socket
   */
  @Throws(IOException::class)
  protected fun Socket.send(socketMessage: SocketMessage) {
    this.outputStream.write(socketMessage.getBytes())
  }

  /**
   * Closes the socket if the socket is not closed.
   */
  private fun Socket.cleanup() {
    Timber.v("Cleaning auxiliary socket")
    if (!this.isClosed) {
      try {
        this.close()
      } catch (ex: IOException) {
        Timber.v(ex, "Failed to clause the auxiliary socket")
      }
    }
  }

  data class ServiceMessage(val message: String, val socket: Socket)
}
