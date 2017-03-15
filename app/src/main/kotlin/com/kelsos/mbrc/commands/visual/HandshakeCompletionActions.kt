package com.kelsos.mbrc.commands.visual

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.model.ConnectionModel
import com.kelsos.mbrc.services.SocketService
import com.kelsos.mbrc.ui.navigation.library.LibrarySyncInteractor
import javax.inject.Inject

class HandshakeCompletionActions
@Inject
constructor(
  private val service: SocketService,
  private val connectionModel: ConnectionModel,
  private val syncInteractor: LibrarySyncInteractor
) : ICommand {

  override fun execute(e: IEvent) {
    val isComplete = e.data as Boolean
    connectionModel.setHandShakeDone(isComplete)

    if (!isComplete) {
      return
    }

    service.sendData(SocketMessage.create(Protocol.INIT))
    service.sendData(SocketMessage.create(Protocol.PluginVersion))
    syncInteractor.sync(true)
  }
}
