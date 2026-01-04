package com.kelsos.mbrc.core.networking.protocol.usecases

import com.kelsos.mbrc.core.networking.protocol.actions.ProtocolPingHandle
import com.kelsos.mbrc.core.networking.protocol.actions.ProtocolVersionUpdate
import com.kelsos.mbrc.core.networking.protocol.actions.SimpleLogCommand
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateCover
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateLastFm
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateLfmRating
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateLyrics
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateMute
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateNowPlayingDetails
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateNowPlayingList
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateNowPlayingTrack
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateNowPlayingTrackMoved
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateNowPlayingTrackRemoval
import com.kelsos.mbrc.core.networking.protocol.actions.UpdatePlayState
import com.kelsos.mbrc.core.networking.protocol.actions.UpdatePlaybackPositionCommand
import com.kelsos.mbrc.core.networking.protocol.actions.UpdatePlayerStatus
import com.kelsos.mbrc.core.networking.protocol.actions.UpdatePluginVersionCommand
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateRating
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateRepeat
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateShuffle
import com.kelsos.mbrc.core.networking.protocol.actions.UpdateVolume
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolAction
import kotlin.reflect.KClass

interface CommandFactory {
  fun create(protocol: Protocol): ProtocolAction
}

/**
 * Factory that creates ProtocolAction instances based on Protocol type.
 * Uses a resolver function to obtain instances, avoiding direct Koin dependency.
 */
class CommandFactoryImpl(private val resolver: (KClass<out ProtocolAction>) -> ProtocolAction) :
  CommandFactory {
  private val protocolActionMapping =
    mapOf(
      Protocol.NowPlayingTrack to UpdateNowPlayingTrack::class,
      Protocol.NowPlayingCover to UpdateCover::class,
      Protocol.NowPlayingRating to UpdateRating::class,
      Protocol.PlayerStatus to UpdatePlayerStatus::class,
      Protocol.PlayerState to UpdatePlayState::class,
      Protocol.PlayerRepeat to UpdateRepeat::class,
      Protocol.PlayerVolume to UpdateVolume::class,
      Protocol.PlayerMute to UpdateMute::class,
      Protocol.PlayerShuffle to UpdateShuffle::class,
      Protocol.PlayerScrobble to UpdateLastFm::class,
      Protocol.NowPlayingLyrics to UpdateLyrics::class,
      Protocol.NowPlayingLfmRating to UpdateLfmRating::class,
      Protocol.NowPlayingDetails to UpdateNowPlayingDetails::class,
      Protocol.NowPlayingListRemove to UpdateNowPlayingTrackRemoval::class,
      Protocol.NowPlayingListMove to UpdateNowPlayingTrackMoved::class,
      Protocol.NowPlayingPosition to UpdatePlaybackPositionCommand::class,
      Protocol.NowPlayingListChanged to UpdateNowPlayingList::class,
      Protocol.PluginVersion to UpdatePluginVersionCommand::class,
      Protocol.Ping to ProtocolPingHandle::class,
      Protocol.Pong to SimpleLogCommand::class,
      Protocol.PlayerNext to SimpleLogCommand::class,
      Protocol.PlayerPrevious to SimpleLogCommand::class,
      Protocol.PlayerPlayPause to SimpleLogCommand::class,
      Protocol.NowPlayingListPlay to SimpleLogCommand::class,
      Protocol.PlaylistPlay to SimpleLogCommand::class,
      Protocol.ProtocolTag to ProtocolVersionUpdate::class
    )

  private fun resolveAction(protocol: Protocol): ProtocolAction? {
    val commandClass = protocolActionMapping[protocol]
    return commandClass?.let { resolver(it) }
  }

  override fun create(protocol: Protocol): ProtocolAction {
    val resolvedAction = resolveAction(protocol)
    return resolvedAction ?: error("Not supported message context $protocol")
  }
}
