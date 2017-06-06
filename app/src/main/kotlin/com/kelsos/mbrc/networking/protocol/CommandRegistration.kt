package com.kelsos.mbrc.networking.protocol

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
import toothpick.Scope

object CommandRegistration {
  fun register(controller: CommandExecutor, scope: Scope) {
    controller.register(Protocol.NowPlayingTrack, scope.getInstance(UpdateNowPlayingTrack::class.java))
    controller.register(Protocol.NowPlayingCover, scope.getInstance(UpdateCover::class.java))
    controller.register(Protocol.NowPlayingRating, scope.getInstance(UpdateRating::class.java))
    controller.register(Protocol.PlayerStatus, scope.getInstance(UpdatePlayerStatus::class.java))
    controller.register(Protocol.PlayerState, scope.getInstance(UpdatePlayState::class.java))
    controller.register(Protocol.PlayerRepeat, scope.getInstance(UpdateRepeat::class.java))
    controller.register(Protocol.PlayerVolume, scope.getInstance(UpdateVolume::class.java))
    controller.register(Protocol.PlayerMute, scope.getInstance(UpdateMute::class.java))
    controller.register(Protocol.PlayerShuffle, scope.getInstance(UpdateShuffle::class.java))
    controller.register(Protocol.PlayerScrobble, scope.getInstance(UpdateLastFm::class.java))
    controller.register(Protocol.NowPlayingLyrics, scope.getInstance(UpdateLyrics::class.java))
    controller.register(Protocol.NowPlayingLfmRating, scope.getInstance(UpdateLfmRating::class.java))
    controller.register(Protocol.NowPlayingListRemove, scope.getInstance(UpdateNowPlayingTrackRemoval::class.java))
    controller.register(Protocol.NowPlayingListMove, scope.getInstance(UpdateNowPlayingTrackMoved::class.java))
    controller.register(Protocol.NowPlayingPosition, scope.getInstance(UpdatePlaybackPositionCommand::class.java))
    controller.register(Protocol.PluginVersion, scope.getInstance(UpdatePluginVersionCommand::class.java))
    controller.register(Protocol.PING, scope.getInstance(ProtocolPingHandle::class.java))
    controller.register(Protocol.PONG, scope.getInstance(ProtocolPongHandle::class.java))
  }

  fun unregister(controller: CommandExecutor) {
    controller.clearCommands()
  }
}

