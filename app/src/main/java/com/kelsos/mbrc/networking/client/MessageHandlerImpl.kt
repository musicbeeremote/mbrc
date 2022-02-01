package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.features.settings.ClientInformationStore
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.CommandFactory
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.protocol.ProtocolAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MessageHandlerImpl(
  private val commandFactory: CommandFactory,
  private val messageQueue: MessageQueue,
  private val uiMessages: UiMessages,
  private val connectionState: ConnectionState,
  private val clientInformationStore: ClientInformationStore,
  private val dispatchers: AppCoroutineDispatchers,
) : MessageHandler {

  private var commands: MutableMap<Protocol, ProtocolAction> = HashMap()

  private suspend fun process(message: SocketMessage) = withContext(dispatchers.network) {
    val context = Protocol.fromString(message.context)

    Timber.v("received message with context -> ${message.context}:${message.data}")

    when (context) {
      Protocol.ClientNotAllowed -> clientNotAllowed()
      Protocol.CommandUnavailable -> uiMessages.messages.emit(UiMessage.PartyModeCommandUnavailable)
      Protocol.UnknownCommand -> Unit
      else -> handle(message, context)
    }
  }

  private suspend fun handle(
    message: SocketMessage,
    context: Protocol
  ) {

    val data = message.data

    if (handshake(context, data)) {
      return
    }

    val status = connectionState.connection.firstOrNull() ?: ConnectionStatus.Off
    if (status == ConnectionStatus.Active) {
      execute(MessageEvent(context, data))
    }
  }

  private suspend fun handshake(
    context: Protocol,
    dataPayload: Any
  ): Boolean = when (context) {
    Protocol.Player -> {
      sendProtocolPayload()
      true
    }
    Protocol.ProtocolTag -> {
      val protocolVersion: Int = try {
        dataPayload.toString().toInt()
      } catch (ignore: Exception) {
        2
      }

      execute(MessageEvent(Protocol.ProtocolTag, protocolVersion))
      connectionState.connection.emit(ConnectionStatus.Active)
      messageQueue.queue(SocketMessage.create(Protocol.Init))
      true
    }
    else -> false
  }

  private suspend fun execute(event: MessageEvent) {
    get(event.type)
      .onSuccess { it.execute(event) }
      .onFailure { Timber.e(it, "Failed to execute command for $event") }
  }

  private fun get(context: Protocol) = runCatching {
    val command = commands[context]
    if (command != null) {
      return@runCatching command
    }
    commandFactory.create(context).also { commandInstance ->
      commands[context] = commandInstance
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

  override fun listen(scope: CoroutineScope, messages: Flow<SocketMessage>) {
    scope.launch {
      messages.collect {
        process(it)
      }
    }
  }

  override fun handleOutgoing(scope: CoroutineScope, send: (SocketMessage) -> Result<Unit>) {
    scope.launch {
      messageQueue.messages.collect { message ->
        withContext(dispatchers.network) {
          val sendResult = send(message)
          if (sendResult.isFailure) {
            Timber.e(checkNotNull(sendResult.exceptionOrNull()), "Send failed")
          }
        }
      }
    }
  }

  override suspend fun startHandshake() {
    messageQueue.queue(SocketMessage.player())
  }
}
