package com.kelsos.mbrc.events

import com.kelsos.mbrc.interfaces.ProtocolMessage

data class MessageEvent(override var type: String = "", override var data: Any = "") : ProtocolMessage {

}