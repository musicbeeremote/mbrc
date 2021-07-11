package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber

class MessageHandlerImpl(
  private val commandExecutor: CommandExecutor,
  private val messageQueue: MessageQueue,
  private val uiMessages: UiMessages,
  private val connectionState: ConnectionState,
  private val clientInformationStore: ClientInformationStore,
  private val dispatchers: AppCoroutineDispatchers,
  private val moshi: Moshi
) : MessageHandler {
  private val adapter by lazy { moshi.adapter(SocketMessage::class.java) }

  override suspend fun handleMessage(rawMessage: String) {
    val replies = rawMessage.split("\r\n".toRegex()).dropLastWhile(String::isEmpty)

    replies.forEach { message -> process(message) }
  }

  private suspend fun process(message: String) = withContext(dispatchers.io) {
    val value = runCatching { adapter.fromJson(message) }
    if (value.isFailure) {
      return@withContext
    }
    val node = checkNotNull(value.getOrThrow()) { "socket message should not be null" }
    val context = Protocol.fromString(node.context)

    Timber.v("received message with context -> ${node.context}:${node.data}")

    when (context) {
      Protocol.ClientNotAllowed -> {
        clientNotAllowed()
        return@withContext
      }
      Protocol.CommandUnavailable -> {
        uiMessages.messages.emit(UiMessage.PartyModeCommandUnavailable)
        return@withContext
      }
      Protocol.UnknownCommand -> {
        return@withContext
      }
      else -> {
        val status = connectionState.connection.firstOrNull() ?: ConnectionStatus.Off

        val dataPayload = node.data

        if (context == Protocol.Player) {
          sendProtocolPayload()
          return@withContext
        } else if (context == Protocol.ProtocolTag) {
          val protocolVersion: Int = try {
            dataPayload.toString().toInt()
          } catch (ignore: Exception) {
            2
          }

          commandExecutor.queue(MessageEvent(Protocol.ProtocolTag, protocolVersion))
          connectionState.connection.emit(ConnectionStatus.Active)
          messageQueue.queue(SocketMessage.create(Protocol.Init))
          return@withContext
        }

        if (status == ConnectionStatus.Active) {
          commandExecutor.queue(MessageEvent(context, dataPayload))
        }
      }
    }
  }

  private suspend fun sendProtocolPayload() {
    val payload = ProtocolPayload(clientInformationStore.getClientId()).apply {
      noBroadcast = false
      protocolVersion = Protocol.ProtocolVersionNumber
    }
    messageQueue.queue(SocketMessage.create(Protocol.ProtocolTag, payload))
  }

  private suspend fun clientNotAllowed() {
    uiMessages.messages.emit(UiMessage.NotAllowed)
    connectionState.connection.emit(ConnectionStatus.Off)
  }

  override fun start() {
    commandExecutor.start()
  }

  override fun stop() {
    commandExecutor.stop()
  }
}
