package com.kelsos.mbrc.networking.client

import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.networking.client.UiMessageQueue.Companion.NOT_ALLOWED
import com.kelsos.mbrc.networking.client.UiMessageQueue.Companion.PARTY_MODE_COMMAND_UNAVAILABLE
import com.kelsos.mbrc.networking.connections.Connection.ACTIVE
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.preferences.ClientInformationStore
import com.squareup.moshi.Moshi
import timber.log.Timber

class MessageHandlerImpl(
  private val commandExecutor: CommandExecutor,
  private val messageQueue: MessageQueue,
  private val uiMessageQueue: UiMessageQueue,
  private val connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider,
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
    val context = node.context

    Timber.v("received message with context -> $context")

    if (context == Protocol.ClientNotAllowed) {
      clientNotAllowed()
      return
    } else if (context == Protocol.CommandUnavailable) {
      uiMessageQueue.dispatch(PARTY_MODE_COMMAND_UNAVAILABLE)
      return
    }

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

      // model.pluginProtocol = protocolVersion
      connectionStatusLiveDataProvider.active()
      handshakeComplete()
      // bus.post(StartLibrarySyncEvent())
      return
    }

    if (connectionStatus.status == ACTIVE) {
      commandExecutor.processEvent(MessageEvent(context, dataPayload))
    }
  }

  private fun sendProtocolPayload() {
    val payload = ProtocolPayload(clientInformationStore.getClientId()).apply {
      noBroadcast = false
      protocolVersion = Protocol.ProtocolVersionNumber
    }
    messageQueue.queue(SocketMessage.create(Protocol.ProtocolTag, payload))
  }

  private fun clientNotAllowed() {
    uiMessageQueue.dispatch(NOT_ALLOWED)
    // bus.post(ChangeConnectionStateEvent(STOP))
    connectionStatusLiveDataProvider.disconnected()
  }

  fun handshakeComplete() {
    messageQueue.queue(SocketMessage.create(Protocol.INIT))
  }

  override fun start() {
    commandExecutor.start()
  }

  override fun stop() {
    commandExecutor.stop()
  }
}