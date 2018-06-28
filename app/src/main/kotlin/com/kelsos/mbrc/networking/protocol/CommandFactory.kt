package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPingHandle
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPongHandle
import com.kelsos.mbrc.networking.protocol.commands.UpdateCover
import com.kelsos.mbrc.networking.protocol.commands.UpdateLastFm
import com.kelsos.mbrc.networking.protocol.commands.UpdateLfmRating
import com.kelsos.mbrc.networking.protocol.commands.UpdateLyrics
import com.kelsos.mbrc.networking.protocol.commands.UpdateMute
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrack
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrackMoved
import com.kelsos.mbrc.networking.protocol.commands.UpdateNowPlayingTrackRemoval
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlayState
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlaybackPositionCommand
import com.kelsos.mbrc.networking.protocol.commands.UpdatePlayerStatus
import com.kelsos.mbrc.networking.protocol.commands.UpdatePluginVersionCommand
import com.kelsos.mbrc.networking.protocol.commands.UpdateRating
import com.kelsos.mbrc.networking.protocol.commands.UpdateRepeat
import com.kelsos.mbrc.networking.protocol.commands.UpdateShuffle
import com.kelsos.mbrc.networking.protocol.commands.UpdateVolume
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CommandFactoryImpl : CommandFactory, KoinComponent {

  override fun create(@Protocol.Context context: String): ICommand {
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
      else -> error("Not supported message context $context")
    }
  }
}

interface CommandFactory {
  fun create(@Protocol.Context context: String): ICommand
}
