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

@OptIn(KoinApiExtension::class)
class CommandFactoryImpl : CommandFactory, KoinComponent {

  override fun create(protocol: Protocol): ProtocolAction = when (protocol) {
    Protocol.NowPlayingTrack -> getKoin().get<UpdateNowPlayingTrack>()
    Protocol.NowPlayingCover -> getKoin().get<UpdateCover>()
    Protocol.NowPlayingRating -> getKoin().get<UpdateRating>()
    Protocol.PlayerStatus -> getKoin().get<UpdatePlayerStatus>()
    Protocol.PlayerState -> getKoin().get<UpdatePlayState>()
    Protocol.PlayerRepeat -> getKoin().get<UpdateRepeat>()
    Protocol.PlayerVolume -> getKoin().get<UpdateVolume>()
    Protocol.PlayerMute -> getKoin().get<UpdateMute>()
    Protocol.PlayerShuffle -> getKoin().get<UpdateShuffle>()
    Protocol.PlayerScrobble -> getKoin().get<UpdateLastFm>()
    Protocol.NowPlayingLyrics -> getKoin().get<UpdateLyrics>()
    Protocol.NowPlayingLfmRating -> getKoin().get<UpdateLfmRating>()
    Protocol.NowPlayingListRemove -> getKoin().get<UpdateNowPlayingTrackRemoval>()
    Protocol.NowPlayingListMove -> getKoin().get<UpdateNowPlayingTrackMoved>()
    Protocol.NowPlayingPosition -> getKoin().get<UpdatePlaybackPositionCommand>()
    Protocol.PluginVersion -> getKoin().get<UpdatePluginVersionCommand>()
    Protocol.Ping -> getKoin().get<ProtocolPingHandle>()
    Protocol.Pong -> getKoin().get<ProtocolPongHandle>()
    Protocol.ProtocolTag -> getKoin().get<ProtocolVersionUpdate>()
    else -> error("Not supported message context $protocol")
  }
}

interface CommandFactory {
  fun create(protocol: Protocol): ProtocolAction
}
