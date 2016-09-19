package com.kelsos.mbrc.commands.visual

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.UpdatePosition
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import javax.inject.Inject

class UpdatePlaybackPositionCommand
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val oNode = e.data as ObjectNode
    bus.post(UpdatePosition(oNode.path("current").asInt(), oNode.path("total").asInt()))
  }
}
