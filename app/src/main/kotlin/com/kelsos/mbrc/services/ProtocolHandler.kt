package com.kelsos.mbrc.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.model.MainDataModel
import timber.log.Timber
import java.io.IOException
import java.lang.Double.parseDouble
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

  fun preProcessIncoming(incoming: String) {
    try {
      val replies = incoming.split("\r\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      for (reply in replies) {

        Timber.v("message -> %s", reply)

        val node = mapper.readValue(reply, JsonNode::class.java)
        val context = node.path("context").textValue()

        if (context.contains(Protocol.ClientNotAllowed)) {
          bus.post(MessageEvent(ProtocolEventType.InformClientNotAllowed))
          return
        }

        if (!isHandshakeComplete) {
          if (context.contains(Protocol.Player)) {
            bus.post(MessageEvent(ProtocolEventType.InitiateProtocolRequest))
          } else if (context.contains(Protocol.ProtocolTag)) {

            val protocolVersion: Double
            protocolVersion = try {
              parseDouble(node.path(Const.DATA).asText())
            } catch (ignore: Exception) {
              2.0
            }

            model.pluginProtocol = protocolVersion
            isHandshakeComplete = true
            bus.post(MessageEvent(ProtocolEventType.HandshakeComplete, true))
          } else {
            return
          }
        }

        bus.post(MessageEvent(context, node.path(Const.DATA)))
      }
    } catch (e: IOException) {
      Timber.d(e, "Incoming preprocessor")
      Timber.d("While processing: %s", incoming)
    }

  }
}
