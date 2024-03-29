package com.kelsos.mbrc.networking

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.data.DeserializationAdapter
import com.kelsos.mbrc.data.SerializationAdapter
import com.kelsos.mbrc.features.settings.ClientInformationStore
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.toSocketAddress
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.Socket
import java.nio.charset.Charset

class RequestManagerImpl(
  private val serializationAdapter: SerializationAdapter,
  private val deserializationAdapter: DeserializationAdapter,
  private val clientInformationStore: ClientInformationStore,
  private val repository: ConnectionRepository,
  private val dispatchers: AppCoroutineDispatchers
) : RequestManager {

  override suspend fun openConnection(handshake: Boolean): ActiveConnection =
    withContext(dispatchers.network) {
      val firstMessage = if (handshake) SocketMessage.create(Protocol.Player, "Android") else null
      val socket = connect(firstMessage)

      val inputStream = socket.getInputStream()
      val bufferedReader = inputStream.bufferedReader(Charset.defaultCharset())

      while (handshake) {
        val line = bufferedReader.readLine()
        if (line.isNullOrEmpty()) {
          break
        }

        val message = deserializationAdapter.objectify(line, SocketMessage::class)

        val context = Protocol.fromString(message.context)
        Timber.v("incoming context => ${context.context}")
        if (Protocol.Player == context) {
          val payload = getProtocolPayload()
          socket.send(SocketMessage.create(Protocol.ProtocolTag, payload))
        } else if (Protocol.ProtocolTag == context) {
          Timber.v("socket handshake complete")
          break
        }
      }

      return@withContext ActiveConnection(socket, bufferedReader)
    }

  private suspend fun getProtocolPayload(): ProtocolPayload {
    return ProtocolPayload(
      noBroadcast = true,
      protocolVersion = Protocol.ProtocolVersionNumber,
      clientId = clientInformationStore.getClientId()
    )
  }

  override suspend fun request(connection: ActiveConnection, message: SocketMessage): String =
    withContext(dispatchers.network) {
      connection.send(message.getBytes())
      val readLine = connection.readLine()
      return@withContext readLine.ifEmpty {
        connection.readLine()
      }
    }

  private fun connect(firstMessage: SocketMessage?): Socket {
    val connectionSettings = checkNotNull(repository.getDefault())

    try {
      Timber.v("Preparing connection to $connectionSettings")
      val socketAddress = connectionSettings.toSocketAddress()
      Timber.v("Creating new socket")

      val socket = Socket()
      socket.soTimeout = SO_TIMEOUT_MS
      socket.connect(socketAddress)
      firstMessage?.let { message ->
        socket.send(message)
      }
      return socket
    } catch (e: IOException) {
      Timber.v(e, "failed to create socket")
      throw e
    }
  }

  private fun SocketMessage.getBytes(): ByteArray {
    return (serializationAdapter.stringify(this) + "\r\n").toByteArray()
  }

  private fun Socket.send(socketMessage: SocketMessage) {
    this.outputStream.write(socketMessage.getBytes())
  }

  companion object {
    private const val SO_TIMEOUT_MS = 20_000
  }
}
