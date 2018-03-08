package com.kelsos.mbrc.networking.protocol

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.content.activestatus.MainDataModel
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.networking.*
import com.kelsos.mbrc.networking.SocketAction.STOP
import com.kelsos.mbrc.networking.connections.ConnectionStatusModel
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolHandler
@Inject
constructor(
  private val bus: RxBus,
  private val mapper: ObjectMapper,
  private val model: MainDataModel,
  private val connectionStatusModel: ConnectionStatusModel,
  private val clientInformationStore: ClientInformationStore
) {

  init {
    bus.register(this, SocketDataAvailableEvent::class.java, { onIncoming(it) })
    bus.register(this, SocketHandshakeUpdateEvent::class.java, { onHandsakeUpdate(it) })
    connectionStatusModel.setOnConnectedListener {
      bus.post(SendProtocolMessage(SocketMessage.create(Protocol.Player, Protocol.CLIENT_PLATFORM)))
    }
  }

  private fun onHandsakeUpdate(event: SocketHandshakeUpdateEvent) {
    if (!event.done) {
      connectionStatusModel.handshake = false
    }
  }

  private fun onIncoming(event: SocketDataAvailableEvent) {
    preProcessIncoming(event.data)
        .subscribeOn(Schedulers.io())
        .subscribe({
          Timber.v("processing done")
        }) {
          Timber.e(it, "processing error")
        }
  }

  fun preProcessIncoming(incoming: String): Completable {
    return Completable.fromAction {
      val replies = incoming.split("\r\n".toRegex())
          .dropLastWhile(String::isEmpty)
          .toTypedArray()

      replies.forEach {
        Timber.v("message -> %s", it)

        val node = mapper.readValue(it, JsonNode::class.java)
        val context = node.path("context").textValue()

        if (context.contains(Protocol.ClientNotAllowed)) {
          clientNotAllowed()
          return@fromAction
        } else if (context.contains(Protocol.CommandUnavailable)) {
          bus.post(NotifyUser(R.string.party_mode__command_unavailable))
          return@fromAction
        }

        if (!connectionStatusModel.handshake) {
          if (context.contains(Protocol.Player)) {
            sendProtocolPayload()
          } else if (context.contains(Protocol.ProtocolTag)) {

            val protocolVersion: Int = try {
              Integer.parseInt(node.path("data").asText())
            } catch (ignore: Exception) {
              2
            }

            model.pluginProtocol = protocolVersion
            connectionStatusModel.handshake = true
            handshakeComplete()
            bus.post(StartLibrarySyncEvent())
          } else {
            return@fromAction
          }
        }

        bus.post(MessageEvent(context, node.path("data")))
      }
    }
  }

  fun sendProtocolPayload() {
    val payload = ProtocolPayload(clientInformationStore.getClientId())
    payload.noBroadcast = false
    payload.protocolVersion = Protocol.ProtocolVersionNumber
    bus.post(SendProtocolMessage(SocketMessage.create(Protocol.ProtocolTag, payload)))
  }

  fun clientNotAllowed() {
    bus.post(NotifyUser(R.string.notification_not_allowed))
    bus.post(ChangeConnectionStateEvent(STOP))
    connectionStatusModel.disconnected()
  }

  fun handshakeComplete() {
    if (model.pluginProtocol > 2) {
      Timber.v("Sending init request")
      bus.post(SendProtocolMessage(SocketMessage.create(Protocol.INIT)))
    } else {

      Timber.v("Preparing to send requests for state")

      val messages = ArrayList<SocketMessage>().apply {
        add(SocketMessage.create(Protocol.NowPlayingCover))
        add(SocketMessage.create(Protocol.PlayerStatus))
        add(SocketMessage.create(Protocol.NowPlayingTrack))
        add(SocketMessage.create(Protocol.NowPlayingLyrics))
        add(SocketMessage.create(Protocol.NowPlayingPosition))
        add(SocketMessage.create(Protocol.PluginVersion))
      }

      val totalMessages = messages.size.toLong()
      Observable.interval(150, TimeUnit.MILLISECONDS)
          .take(totalMessages)
          .subscribe({ bus.post(SendProtocolMessage(messages.removeAt(0))) }) {
            Timber.v(it, "Failure while sending the init messages")
          }
    }
  }
}