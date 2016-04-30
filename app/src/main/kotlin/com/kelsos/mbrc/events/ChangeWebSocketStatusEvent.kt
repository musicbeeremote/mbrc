package com.kelsos.mbrc.events

import android.support.annotation.IntDef

class ChangeWebSocketStatusEvent private constructor()//no instance
{

  @Action var action: Long = 0
    private set

  @IntDef(CONNECT , DISCONNECT)
  @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
  annotation class Action

  companion object {
    const val CONNECT = 1L
    const val DISCONNECT = 2L

    fun newInstance(@Action action: Long): ChangeWebSocketStatusEvent {
      val event = ChangeWebSocketStatusEvent()
      event.action = action
      return event
    }
  }
}
