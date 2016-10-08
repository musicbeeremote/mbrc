package com.kelsos.mbrc.configuration

import com.kelsos.mbrc.commands.*
import com.kelsos.mbrc.commands.model.*
import com.kelsos.mbrc.commands.visual.*
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.constants.SocketEventType
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.controller.RemoteController
import javax.inject.Inject

object CommandRegistration {
  @Inject fun register(controller: RemoteController) {
    controller.register(ProtocolEventType.ReduceVolume, ReduceVolumeOnRingCommand::class.java)
    controller.register(ProtocolEventType.HandshakeComplete, VisualUpdateHandshakeComplete::class.java)
    controller.register(ProtocolEventType.InformClientNotAllowed, NotifyNotAllowedCommand::class.java)
    controller.register(ProtocolEventType.InformClientPluginOutOfDate,
        NotifyPluginOutOfDateCommand::class.java)
    controller.register(ProtocolEventType.InitiateProtocolRequest, ProtocolRequest::class.java)
    controller.register(ProtocolEventType.PluginVersionCheck, VersionCheckCommand::class.java)
    controller.register(ProtocolEventType.UserAction, ProcessUserAction::class.java)

    controller.register(Protocol.NowPlayingTrack, UpdateNowPlayingTrack::class.java)
    controller.register(Protocol.NowPlayingCover, UpdateCover::class.java)
    controller.register(Protocol.NowPlayingRating, UpdateRating::class.java)
    controller.register(Protocol.PlayerStatus, UpdatePlayerStatus::class.java)
    controller.register(Protocol.PlayerState, UpdatePlayState::class.java)
    controller.register(Protocol.PlayerRepeat, UpdateRepeat::class.java)
    controller.register(Protocol.PlayerVolume, UpdateVolume::class.java)
    controller.register(Protocol.PlayerMute, UpdateMute::class.java)
    controller.register(Protocol.PlayerShuffle, UpdateShuffle::class.java)
    controller.register(Protocol.PlayerScrobble, UpdateLastFm::class.java)
    controller.register(Protocol.NowPlayingLyrics, UpdateLyrics::class.java)
    controller.register(Protocol.NowPlayingLfmRating, UpdateLfmRating::class.java)
    controller.register(Protocol.NowPlayingListRemove, UpdateNowPlayingTrackRemoval::class.java)
    controller.register(Protocol.NowPlayingListMove, UpdateNowPlayingTrackMoved::class.java)
    controller.register(Protocol.NowPlayingPosition, UpdatePlaybackPositionCommand::class.java)
    controller.register(Protocol.PluginVersion, UpdatePluginVersionCommand::class.java)
    controller.register(Protocol.PING, ProtocolPingHandle::class.java)
    controller.register(Protocol.PONG, ProtocolPongHandle::class.java)
    controller.register(Protocol.PlaylistList, UpdatePlaylistList::class.java)

    controller.register(UserInputEventType.SettingsChanged, RestartConnectionCommand::class.java)
    controller.register(UserInputEventType.CancelNotification, CancelNotificationCommand::class.java)
    controller.register(UserInputEventType.StartConnection, InitiateConnectionCommand::class.java)
    controller.register(UserInputEventType.TerminateConnection, TerminateConnectionCommand::class.java)
    controller.register(UserInputEventType.ResetConnection, RestartConnectionCommand::class.java)
    controller.register(UserInputEventType.StartDiscovery, StartDiscoveryCommand::class.java)
    controller.register(UserInputEventType.KeyVolumeUp, KeyVolumeUpCommand::class.java)
    controller.register(UserInputEventType.KeyVolumeDown, KeyVolumeDownCommand::class.java)
    controller.register(SocketEventType.SocketDataAvailable, SocketDataAvailableCommand::class.java)
    controller.register(SocketEventType.SocketStatusChanged, ConnectionStatusChangedCommand::class.java)
    controller.register(SocketEventType.SocketHandshakeUpdate, HandleHandshake::class.java)
  }

  fun unregister(controller: RemoteController) {
    controller.clearCommands()
  }
}
