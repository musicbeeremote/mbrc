package com.kelsos.mbrc.configuration

import com.kelsos.mbrc.commands.*
import com.kelsos.mbrc.commands.model.*
import com.kelsos.mbrc.commands.visual.*
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.constants.SocketEventType
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.controller.RemoteController
import toothpick.Scope

object CommandRegistration {
  fun register(controller: RemoteController, scope: Scope) {
    controller.register(ProtocolEventType.ReduceVolume, scope.getInstance(ReduceVolumeOnRingCommand::class.java))
    controller.register(ProtocolEventType.HandshakeComplete, scope.getInstance(VisualUpdateHandshakeComplete::class.java))
    controller.register(ProtocolEventType.InformClientNotAllowed, scope.getInstance(NotifyNotAllowedCommand::class.java))
    controller.register(ProtocolEventType.InformClientPluginOutOfDate, scope.getInstance(NotifyPluginOutOfDateCommand::class.java))
    controller.register(ProtocolEventType.InitiateProtocolRequest, scope.getInstance(ProtocolRequest::class.java))
    controller.register(ProtocolEventType.PluginVersionCheck, scope.getInstance(VersionCheckCommand::class.java))
    controller.register(ProtocolEventType.UserAction, scope.getInstance(ProcessUserAction::class.java))

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

    controller.register(UserInputEventType.SettingsChanged, scope.getInstance(RestartConnectionCommand::class.java))
    controller.register(UserInputEventType.CancelNotification, scope.getInstance(CancelNotificationCommand::class.java))
    controller.register(UserInputEventType.StartConnection, scope.getInstance(InitiateConnectionCommand::class.java))
    controller.register(UserInputEventType.TerminateConnection, scope.getInstance(TerminateConnectionCommand::class.java))
    controller.register(UserInputEventType.ResetConnection, scope.getInstance(RestartConnectionCommand::class.java))
    controller.register(UserInputEventType.StartDiscovery, scope.getInstance(StartDiscoveryCommand::class.java))
    controller.register(UserInputEventType.KeyVolumeUp, scope.getInstance(KeyVolumeUpCommand::class.java))
    controller.register(UserInputEventType.KeyVolumeDown, scope.getInstance(KeyVolumeDownCommand::class.java))
    controller.register(SocketEventType.SocketDataAvailable, scope.getInstance(SocketDataAvailableCommand::class.java))
    controller.register(SocketEventType.SocketStatusChanged, scope.getInstance(ConnectionStatusChangedCommand::class.java))
    controller.register(SocketEventType.SocketHandshakeUpdate, scope.getInstance(HandleHandshake::class.java))
  }

  fun unregister(controller: RemoteController) {
    controller.clearCommands()
  }
}
