package com.kelsos.mbrc.networking.protocol

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.networking.protocol.commands.CancelNotificationCommand
import com.kelsos.mbrc.networking.protocol.commands.HandshakeCompletionActions
import com.kelsos.mbrc.networking.protocol.commands.InitiateConnectionCommand
import com.kelsos.mbrc.networking.protocol.commands.KeyVolumeDownCommand
import com.kelsos.mbrc.networking.protocol.commands.KeyVolumeUpCommand
import com.kelsos.mbrc.networking.protocol.commands.NotifyNotAllowedCommand
import com.kelsos.mbrc.networking.protocol.commands.ProcessUserAction
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPingHandle
import com.kelsos.mbrc.networking.protocol.commands.ProtocolPongHandle
import com.kelsos.mbrc.networking.protocol.commands.ProtocolRequest
import com.kelsos.mbrc.networking.protocol.commands.ReduceVolumeOnRingCommand
import com.kelsos.mbrc.networking.protocol.commands.RestartConnectionCommand
import com.kelsos.mbrc.networking.protocol.commands.StartDiscoveryCommand
import com.kelsos.mbrc.networking.protocol.commands.TerminateConnectionCommand
import com.kelsos.mbrc.networking.protocol.commands.TerminateServiceCommand
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
import com.kelsos.mbrc.networking.protocol.commands.VersionCheckCommand
import toothpick.Scope

object CommandRegistration {
  fun register(controller: CommandExecutor, scope: Scope) {
    controller.register(
      ProtocolEventType.ReduceVolume,
      scope.getInstance(ReduceVolumeOnRingCommand::class.java)
    )
    controller.register(
      ProtocolEventType.HandshakeComplete,
      scope.getInstance(HandshakeCompletionActions::class.java)
    )
    controller.register(
      ProtocolEventType.InformClientNotAllowed,
      scope.getInstance(NotifyNotAllowedCommand::class.java)
    )
    controller.register(
      ProtocolEventType.InitiateProtocolRequest,
      scope.getInstance(ProtocolRequest::class.java)
    )
    controller.register(
      ProtocolEventType.PluginVersionCheck,
      scope.getInstance(VersionCheckCommand::class.java)
    )
    controller.register(
      ProtocolEventType.UserAction,
      scope.getInstance(ProcessUserAction::class.java)
    )

    controller.register(
      Protocol.NowPlayingTrack,
      scope.getInstance(UpdateNowPlayingTrack::class.java)
    )
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
    controller.register(
      Protocol.NowPlayingLfmRating,
      scope.getInstance(UpdateLfmRating::class.java)
    )
    controller.register(
      Protocol.NowPlayingListRemove,
      scope.getInstance(UpdateNowPlayingTrackRemoval::class.java)
    )
    controller.register(
      Protocol.NowPlayingListMove,
      scope.getInstance(UpdateNowPlayingTrackMoved::class.java)
    )
    controller.register(
      Protocol.NowPlayingPosition,
      scope.getInstance(UpdatePlaybackPositionCommand::class.java)
    )
    controller.register(
      Protocol.PluginVersion,
      scope.getInstance(UpdatePluginVersionCommand::class.java)
    )
    controller.register(Protocol.PING, scope.getInstance(ProtocolPingHandle::class.java))
    controller.register(Protocol.PONG, scope.getInstance(ProtocolPongHandle::class.java))

    controller.register(
      UserInputEventType.SettingsChanged,
      scope.getInstance(RestartConnectionCommand::class.java)
    )
    controller.register(
      UserInputEventType.CancelNotification,
      scope.getInstance(CancelNotificationCommand::class.java)
    )
    controller.register(
      UserInputEventType.StartConnection,
      scope.getInstance(InitiateConnectionCommand::class.java)
    )
    controller.register(
      UserInputEventType.TerminateConnection,
      scope.getInstance(TerminateConnectionCommand::class.java)
    )
    controller.register(
      UserInputEventType.ResetConnection,
      scope.getInstance(RestartConnectionCommand::class.java)
    )
    controller.register(
      UserInputEventType.StartDiscovery,
      scope.getInstance(StartDiscoveryCommand::class.java)
    )
    controller.register(
      UserInputEventType.KeyVolumeUp,
      scope.getInstance(KeyVolumeUpCommand::class.java)
    )
    controller.register(
      UserInputEventType.KeyVolumeDown,
      scope.getInstance(KeyVolumeDownCommand::class.java)
    )
    controller.register(
      ProtocolEventType.TerminateService,
      scope.getInstance(TerminateServiceCommand::class.java)
    )
  }

  fun unregister(controller: CommandExecutor) {
    controller.clearCommands()
  }
}
