package com.kelsos.mbrc.events

import com.fasterxml.jackson.databind.node.TextNode
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.interfaces.IEvent

data class MessageEvent(override var type: String = "", override var data: Any = "") : IEvent {

  override val dataString: String
    get() {
      val result: String
      if (data.javaClass == TextNode::class.java) {
        result = (data as TextNode).asText()
      } else if (data.javaClass == String::class.java) {
        result = data as String
      } else {
        result = ""
      }
      return result
    }

  companion object {
    fun action(data: UserAction): MessageEvent {
      return MessageEvent(ProtocolEventType.UserAction, data)
    }
  }
}
