package com.kelsos.mbrc.events

import com.kelsos.mbrc.networking.protocol.Protocol

class UserAction(val context: String, val data: Any) {
  companion object {

    fun create(context: String): UserAction {
      return UserAction(context, true)
    }

    fun create(context: String, data: Any): UserAction {
      return UserAction(context, data)
    }

    fun toggle(context: String): UserAction {
      return UserAction(context, Protocol.TOGGLE)
    }


  }
}
