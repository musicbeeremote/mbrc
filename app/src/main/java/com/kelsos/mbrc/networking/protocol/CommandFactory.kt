package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.protocol.ProtocolAction
import com.kelsos.mbrc.protocol.ProtocolPingHandle
import com.kelsos.mbrc.protocol.ProtocolPongHandle
import com.kelsos.mbrc.protocol.ProtocolVersionUpdate
import com.kelsos.mbrc.protocol.UpdateCover
import com.kelsos.mbrc.protocol.UpdateLastFm
import com.kelsos.mbrc.protocol.UpdateLfmRating
import com.kelsos.mbrc.protocol.UpdateLyrics
import com.kelsos.mbrc.protocol.UpdateMute
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrack
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrackMoved
import com.kelsos.mbrc.protocol.UpdateNowPlayingTrackRemoval
import com.kelsos.mbrc.protocol.UpdatePlayState
import com.kelsos.mbrc.protocol.UpdatePlaybackPositionCommand
import com.kelsos.mbrc.protocol.UpdatePlayerStatus
import com.kelsos.mbrc.protocol.UpdatePluginVersionCommand
import com.kelsos.mbrc.protocol.UpdateRating
import com.kelsos.mbrc.protocol.UpdateRepeat
import com.kelsos.mbrc.protocol.UpdateShuffle
import com.kelsos.mbrc.protocol.UpdateVolume
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@OptIn(KoinApiExtension::class)
class CommandFactoryImpl : CommandFactory, KoinComponent {

  override fun create(@Protocol.Context context: String): ProtocolAction {
    return when (context) {
      Protocol.NowPlayingTrack -> get<UpdateNowPlayingTrack>()
      Protocol.NowPlayingCover -> get<UpdateCover>()
      Protocol.NowPlayingRating -> get<UpdateRating>()
      Protocol.PlayerStatus -> get<UpdatePlayerStatus>()
      Protocol.PlayerState -> get<UpdatePlayState>()
      Protocol.PlayerRepeat -> get<UpdateRepeat>()
      Protocol.PlayerVolume -> get<UpdateVolume>()
      Protocol.PlayerMute -> get<UpdateMute>()
      Protocol.PlayerShuffle -> get<UpdateShuffle>()
      Protocol.PlayerScrobble -> get<UpdateLastFm>()
      Protocol.NowPlayingLyrics -> get<UpdateLyrics>()
      Protocol.NowPlayingLfmRating -> get<UpdateLfmRating>()
      Protocol.NowPlayingListRemove -> get<UpdateNowPlayingTrackRemoval>()
      Protocol.NowPlayingListMove -> get<UpdateNowPlayingTrackMoved>()
      Protocol.NowPlayingPosition -> get<UpdatePlaybackPositionCommand>()
      Protocol.PluginVersion -> get<UpdatePluginVersionCommand>()
      Protocol.PING -> get<ProtocolPingHandle>()
      Protocol.PONG -> get<ProtocolPongHandle>()
      Protocol.ProtocolTag -> get<ProtocolVersionUpdate>()
      else -> error("Not supported message context $context")
    }
  }
}

interface CommandFactory {
  fun create(@Protocol.Context context: String): ProtocolAction
}
