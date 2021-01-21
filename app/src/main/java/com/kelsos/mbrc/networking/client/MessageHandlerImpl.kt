package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusState
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MessageHandlerImpl(
  private val commandExecutor: CommandExecutor,
  private val messageQueue: MessageQueue,
  private val uiMessageQueue: UiMessageQueue,
  private val connectionStatusLiveDataProvider: ConnectionStatusState,
  private val clientInformationStore: ClientInformationStore,
  private val moshi: Moshi
) : MessageHandler {
  private val adapter by lazy { moshi.adapter(SocketMessage::class.java) }

  override fun handleMessage(incoming: String) {
    val replies = incoming.split("\r\n".toRegex()).dropLastWhile(String::isEmpty)

    replies.forEach { message ->
      process(message)
    }
  }

  private fun process(message: String) {
    val node = checkNotNull(adapter.fromJson(message)) { "socket message should not be null" }
    val context = Protocol.fromString(node.context)

    Timber.v("received message with context -> $context")

    when (context) {
      Protocol.ClientNotAllowed -> {
        clientNotAllowed()
        return
      }
      Protocol.CommandUnavailable -> {
        uiMessageQueue.emit(UiMessage.PartyModeCommandNotAvailable)
        return
      }
      Protocol.UnknownCommand -> {
        return
      }
      else -> {
        val connectionStatus = connectionStatusLiveDataProvider.requireValue()

        val dataPayload = node.data

        if (context == Protocol.Player) {
          sendProtocolPayload()
          return
        } else if (context == Protocol.ProtocolTag) {
          val protocolVersion: Int = try {
            dataPayload.toString().toInt()
          } catch (ignore: Exception) {
            2
          }

          commandExecutor.processEvent(MessageEvent(Protocol.ProtocolTag, protocolVersion))
          connectionStatusLiveDataProvider.active()
          messageQueue.queue(SocketMessage.create(Protocol.Init))
          return
        }

        if (connectionStatus == ConnectionStatus.Active) {
          commandExecutor.processEvent(MessageEvent(context, dataPayload))
        }
      }
    }
  }

  private fun sendProtocolPayload() {
    val clientId = runBlocking { clientInformationStore.getClientId() }
    val payload = ProtocolPayload(clientId).apply {
      noBroadcast = false
      protocolVersion = Protocol.ProtocolVersionNumber
    }
    messageQueue.queue(SocketMessage.create(Protocol.ProtocolTag, payload))
  }

  private fun clientNotAllowed() {
    uiMessageQueue.emit(UiMessage.NotAllowed)
    connectionStatusLiveDataProvider.disconnected()
  }

  override fun start() {
    commandExecutor.start()
  }

  override fun stop() {
    commandExecutor.stop()
  }
}
