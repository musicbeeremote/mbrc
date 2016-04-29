package com.kelsos.mbrc.events

import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.interfaces.IEvent

class MessageEvent private constructor(@UserInputEventType.Event override val type: String) : IEvent {

  companion object {
    fun newInstance(@UserInputEventType.Event type: String): MessageEvent {
      return MessageEvent(type)
    }
  }
}
