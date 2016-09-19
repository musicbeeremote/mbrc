package com.kelsos.mbrc.commands

import com.kelsos.mbrc.enums.SocketAction
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.services.SocketService
import javax.inject.Inject

class RestartConnectionCommand
@Inject constructor(private val socket: SocketService) : ICommand {

  override fun execute(e: IEvent) {
    socket.socketManager(SocketAction.RESET)
  }
}
