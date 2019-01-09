package com.kelsos.mbrc.networking.protocol.commands

import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.interfaces.ProtocolMessage
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingMoveResponse
import com.kelsos.mbrc.networking.protocol.responses.NowPlayingTrackRemoveResponse
import com.kelsos.mbrc.networking.protocol.responses.Position
import com.squareup.moshi.Moshi

class UpdateNowPlayingTrackMoved(
  private val moshi: Moshi
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(NowPlayingMoveResponse::class.java)
    val response = adapter.fromJsonValue(message.data)
  }
}

class UpdateNowPlayingTrackRemoval(
  private val moshi: Moshi
) : ICommand {
  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(NowPlayingTrackRemoveResponse::class.java)
    val response = adapter.fromJsonValue(message.data)
  }
}

class UpdatePlaybackPositionCommand(
  private val moshi: Moshi,
  private val trackPositionLiveDataProvider: TrackPositionLiveDataProvider
) : ICommand {

  override fun execute(message: ProtocolMessage) {
    val adapter = moshi.adapter(Position::class.java)
    val response = adapter.fromJsonValue(message.data) ?: return

    trackPositionLiveDataProvider.update(
      PlayingPosition(
        response.current,
        response.total
      )
    )
  }
}
