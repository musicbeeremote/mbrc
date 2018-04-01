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
import timber.log.Timber
import javax.inject.Inject

class MessageHandlerImpl
@Inject
constructor(
  private val commandExecutor: CommandExecutor,
  private val messageDeserializer: MessageDeserializer,
  private val messageQueue: MessageQueue,
  private val uiMessageQueue: UiMessageQueue,
  private val connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider,
  private val clientInformationStore: ClientInformationStore
) : MessageHandler {
  override fun handleMessage(incoming: String) {
    val replies = incoming.split("\r\n".toRegex()).dropLastWhile(String::isEmpty)

    replies.forEach { message ->
      Timber.v("message -> $message" )

      val node = messageDeserializer.deserialize(message)
      val context = node.path("context").textValue()

      if (context == Protocol.ClientNotAllowed) {
        clientNotAllowed()
        return
      } else if (context == Protocol.CommandUnavailable) {
        uiMessageQueue.dispatch(PARTY_MODE_COMMAND_UNAVAILABLE)
        return
      }

      val connectionStatus = connectionStatusLiveDataProvider.requireValue()

      if (connectionStatus.status != ACTIVE) {
        if (context == Protocol.Player) {
          sendProtocolPayload()
        } else if (context == Protocol.ProtocolTag) {

          val protocolVersion: Int = try {
            node.path("data").asText().toInt()
          } catch (ignore: Exception) {
            2
          }

          //model.pluginProtocol = protocolVersion
          connectionStatusLiveDataProvider.active()
          handshakeComplete()
          //bus.post(StartLibrarySyncEvent())
        }

        return
      }

      commandExecutor.processEvent(MessageEvent(context, node.path("data")))
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
    //bus.post(ChangeConnectionStateEvent(STOP))
    connectionStatusLiveDataProvider.disconnected()
  }

  fun handshakeComplete() {
    messageQueue.queue(SocketMessage.create(Protocol.INIT))
  }
}