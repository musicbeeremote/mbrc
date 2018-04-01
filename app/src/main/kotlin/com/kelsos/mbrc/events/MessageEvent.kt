package com.kelsos.mbrc.events

import com.fasterxml.jackson.databind.node.TextNode
import com.kelsos.mbrc.interfaces.IEvent

data class MessageEvent(override var type: String = "", override var data: Any = "") : IEvent {

  override val dataString: String
    get() {
      return when {
        data.javaClass == TextNode::class.java -> (data as TextNode).asText()
        data.javaClass == String::class.java -> data as String
        else -> ""
      }
    }
}