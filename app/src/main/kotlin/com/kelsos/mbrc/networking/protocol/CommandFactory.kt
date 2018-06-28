package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.interfaces.ICommand
import com.kelsos.mbrc.networking.protocol.commands.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

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
