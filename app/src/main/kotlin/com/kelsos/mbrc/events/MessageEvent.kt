package com.kelsos.mbrc.events

import com.kelsos.mbrc.constants.UserInputEventType.Event
import com.kelsos.mbrc.interfaces.IEvent

class MessageEvent constructor(@Event override val type: String) : IEvent
