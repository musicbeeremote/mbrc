package com.kelsos.mbrc.networking.protocol.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.kelsos.mbrc.content.activestatus.TrackPositionData
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.IEvent
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingMoveResponse
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingTrackRemoveResponse
import com.kelsos.mbrc.networking.protocol.responses.Position
import javax.inject.Inject

class UpdateNowPlayingTrackMoved
@Inject
constructor(
  private val mapper: ObjectMapper
) : ICommand {

  override fun execute(e: IEvent) {
    val response: NowPlayingMoveResponse = mapper.treeToValue(e.data as JsonNode) ?: return
  }
}

class UpdateNowPlayingTrackRemoval
@Inject
constructor(
  private val mapper: ObjectMapper
) : ICommand {
  override fun execute(e: IEvent) {
    val response: NowPlayingTrackRemoveResponse = mapper.treeToValue(e.data as JsonNode) ?: return
  }
}

class UpdatePlaybackPositionCommand
@Inject constructor(
  private val mapper: ObjectMapper,
  private val trackPositionLiveDataProvider: TrackPositionLiveDataProvider
) : ICommand {

  override fun execute(e: IEvent) {
    val response: Position = mapper.treeToValue(e.data as JsonNode) ?: return

    trackPositionLiveDataProvider.update(
      TrackPositionData(
        response.current,
        response.total
      )
    )
  }
}
