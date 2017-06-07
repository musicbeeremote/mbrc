package com.kelsos.mbrc.events

import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol.Context

class UserAction(@Context val context: String, val data: Any) {
  companion object {

    fun create(@Context context: String): UserAction {
      return UserAction(context, true)
    }

    fun create(@Context context: String, data: Any): UserAction {
      return UserAction(context, data)
    }

    fun toggle(@Context context: String): UserAction {
      return UserAction(context, Protocol.TOGGLE)
    }
  }
}
