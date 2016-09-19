package com.kelsos.mbrc.commands

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import timber.log.Timber

class ProtocolPongHandle : ICommand {
  override fun execute(e: IEvent) {
    Timber.d(e.data.toString())
  }
}
