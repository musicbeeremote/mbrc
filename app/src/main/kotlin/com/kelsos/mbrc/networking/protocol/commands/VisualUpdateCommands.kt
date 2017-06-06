package com.kelsos.mbrc.networking.protocol.commands

import com.fasterxml.jackson.databind.node.ObjectNode
import com.kelsos.mbrc.events.TrackMoved
import com.kelsos.mbrc.events.TrackRemoval
import com.kelsos.mbrc.events.UpdatePosition
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import javax.inject.Inject

class UpdateNowPlayingTrackMoved
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val isSuccess: Boolean = node.path("success").asBoolean()
    val from: Int = node.path("from").asInt()
    val to: Int = node.path("to").asInt()
    bus.post(TrackMoved(from, to, isSuccess))
  }
}

class UpdateNowPlayingTrackRemoval
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val node = e.data as ObjectNode
    val index: Int = node.path("index").asInt()
    val isSuccess: Boolean = node.path("success").asBoolean()
    bus.post(TrackRemoval(index, isSuccess))
  }
}

class UpdatePlaybackPositionCommand
@Inject constructor(private val bus: RxBus) : ICommand {

  override fun execute(e: IEvent) {
    val oNode = e.data as ObjectNode
    bus.post(UpdatePosition(oNode.path("current").asInt(), oNode.path("total").asInt()))
  }
}
