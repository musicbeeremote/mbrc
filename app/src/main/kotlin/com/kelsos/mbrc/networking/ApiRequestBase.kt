package com.kelsos.mbrc.networking

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.InetAddressMapper
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Observable
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

open class ApiRequestBase(
  private val mapper: ObjectMapper,
  private val repository: ConnectionRepository
) {

  internal fun call(
    firstMessage: SocketMessage = SocketMessage.create(Protocol.Player, "Android")
  ): Observable<ServiceMessage> = Observable.create { emitter ->
    try {
      val socket = connect(firstMessage)

      emitter.setCancellable {
        socket.cleanup()
      }

      val streamReader = InputStreamReader(socket.inputStream, "UTF-8")
      val reader = BufferedReader(streamReader)
      while (true) {
        val line = reader.readLine()
        if (line.isNullOrBlank()) {
          break
        }

        emitter.onNext(ApiRequestBase.ServiceMessage(line, socket))
      }

      Timber.v("complete")
      socket.cleanup()

      emitter.onComplete()
    } catch (ex: Exception) {
      emitter.tryOnError(ex)
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
    val connectionSettings = repository.default ?: throw NullPointerException("no settings")
    try {
      val socketAddress = mapper.map(connectionSettings)
      Timber.v("Creating new socket")

      return Socket().apply {
        soTimeout = 20 * 1000
        connect(socketAddress)
        send(firstMessage)
      }
    } catch (e: IOException) {
      Timber.v("failed to create socket")
      throw e
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