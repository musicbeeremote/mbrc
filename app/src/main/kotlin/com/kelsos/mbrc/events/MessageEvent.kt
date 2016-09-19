package com.kelsos.mbrc.events

import com.fasterxml.jackson.databind.node.TextNode
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.interfaces.IEvent

class MessageEvent : IEvent {
  override var type: String? = null
    private set
  override var data: Any? = null
    private set

  constructor(type: String) {
    this.type = type
    data = ""
  }

  constructor(type: String, data: Any) {
    this.type = type
    this.data = data
  }

  override val dataString: String
    get() {
      var result: String? = null
      if (data!!.javaClass == TextNode::class.java) {
        result = (data as TextNode).asText()
      } else if (data!!.javaClass == String::class.java) {
        result = data as String?
      }
      return result
    }

  companion object {

    fun action(data: Any): MessageEvent {
      return MessageEvent(ProtocolEventType.UserAction, data)
    }
  }
}
