package com.kelsos.mbrc.networking.protocol.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.kelsos.mbrc.events.TrackMovedEvent
import com.kelsos.mbrc.events.TrackRemovalEvent
import com.kelsos.mbrc.events.UpdatePositionEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingMoveResponse
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingTrackRemoveResponse
import com.kelsos.mbrc.networking.protocol.responses.Position
import javax.inject.Inject

class UpdateNowPlayingTrackMoved
@Inject constructor(
  private val bus: RxBus,
  private val mapper: ObjectMapper

) : ICommand {

  override fun execute(e: IEvent) {
    val response: NowPlayingMoveResponse = mapper.treeToValue(e.data as JsonNode)
    bus.post(TrackMovedEvent(response.from, response.to, response.success))
  }
}

class UpdateNowPlayingTrackRemoval
@Inject constructor(
  private val bus: RxBus,
  private val mapper: ObjectMapper
) : ICommand {
  override fun execute(e: IEvent) {
    val response: NowPlayingTrackRemoveResponse = mapper.treeToValue(e.data as JsonNode)
    bus.post(TrackRemovalEvent(response.index, response.success))
  }
}

class UpdatePlaybackPositionCommand
@Inject constructor(
  private val bus: RxBus,
  private val mapper: ObjectMapper
) : ICommand {

  override fun execute(e: IEvent) {
    val response: Position = mapper.treeToValue(e.data as JsonNode)
    bus.post(UpdatePositionEvent(response.current, response.total))
  }
}