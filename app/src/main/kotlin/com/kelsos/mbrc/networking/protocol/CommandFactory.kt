package com.kelsos.mbrc.networking.protocol

import android.app.Application
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
import toothpick.Toothpick
import javax.inject.Inject

class CommandFactoryImpl
@Inject
constructor(application: Application) : CommandFactory {

  private val scope = Toothpick.openScope(application)

  override fun create(@Protocol.Context context: String): ICommand {
    val clazz = when (context) {
      Protocol.NowPlayingTrack -> UpdateNowPlayingTrack::class.java
      Protocol.NowPlayingCover -> UpdateCover::class.java
      Protocol.NowPlayingRating -> UpdateRating::class.java
      Protocol.PlayerStatus -> UpdatePlayerStatus::class.java
      Protocol.PlayerState -> UpdatePlayState::class.java
      Protocol.PlayerRepeat -> UpdateRepeat::class.java
      Protocol.PlayerVolume -> UpdateVolume::class.java
      Protocol.PlayerMute -> UpdateMute::class.java
      Protocol.PlayerShuffle -> UpdateShuffle::class.java
      Protocol.PlayerScrobble -> UpdateLastFm::class.java
      Protocol.NowPlayingLyrics -> UpdateLyrics::class.java
      Protocol.NowPlayingLfmRating -> UpdateLfmRating::class.java
      Protocol.NowPlayingListRemove -> UpdateNowPlayingTrackRemoval::class.java
      Protocol.NowPlayingListMove -> UpdateNowPlayingTrackMoved::class.java
      Protocol.NowPlayingPosition -> UpdatePlaybackPositionCommand::class.java
      Protocol.PluginVersion -> UpdatePluginVersionCommand::class.java
      Protocol.PING -> ProtocolPingHandle::class.java
      Protocol.PONG -> ProtocolPongHandle::class.java
      else -> error("Not supported message context $context")
    }

    return scope.getInstance(clazz)
  }
}

interface CommandFactory {
  fun create(@Protocol.Context context: String): ICommand
}
