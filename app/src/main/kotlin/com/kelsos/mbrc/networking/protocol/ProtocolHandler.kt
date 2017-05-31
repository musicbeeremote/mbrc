package com.kelsos.mbrc.networking.protocol

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.R
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.content.active_status.MainDataModel
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.bus.RxBus
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProtocolHandler
@Inject
constructor(private val bus: RxBus, private val mapper: ObjectMapper, private val model: MainDataModel) {
  private var isHandshakeComplete: Boolean = false

  fun resetHandshake() {
    isHandshakeComplete = false
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
          bus.post(MessageEvent(ProtocolEventType.InformClientNotAllowed))
          return@fromAction
        } else if (context.contains(Protocol.CommandUnavailable)) {
          bus.post(NotifyUser(R.string.party_mode__command_unavailable))
          return@fromAction
        }

        if (!isHandshakeComplete) {
          if (context.contains(Protocol.Player)) {
            bus.post(MessageEvent(ProtocolEventType.InitiateProtocolRequest))
          } else if (context.contains(Protocol.ProtocolTag)) {

            val protocolVersion: Int = try {
              Integer.parseInt(node.path(Const.DATA).asText())
            } catch (ignore: Exception) {
              2
            }

            model.pluginProtocol = protocolVersion
            isHandshakeComplete = true
            bus.post(MessageEvent(ProtocolEventType.HandshakeComplete, true))
          } else {
            return@fromAction
          }
        }

        bus.post(MessageEvent(context, node.path(Const.DATA)))
      }
    }

  }
}
