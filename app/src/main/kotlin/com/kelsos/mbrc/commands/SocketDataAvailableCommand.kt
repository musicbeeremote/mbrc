package com.kelsos.mbrc.commands

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.services.ProtocolHandler
import timber.log.Timber
import javax.inject.Inject

class SocketDataAvailableCommand
@Inject
constructor(
  private val handler: ProtocolHandler
) : ICommand {

  override fun execute(e: IEvent) {
    try {
      handler.preProcessIncoming(e.dataString)
    } catch (e: Exception) {
      Timber.e(e)
    }
  }
}
