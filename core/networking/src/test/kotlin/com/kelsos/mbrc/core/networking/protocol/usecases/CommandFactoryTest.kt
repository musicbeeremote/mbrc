package com.kelsos.mbrc.core.networking.protocol.usecases

import com.google.common.truth.Truth.assertThat
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
import io.mockk.mockk
import kotlin.reflect.KClass
import org.junit.Before
import org.junit.Test

class CommandFactoryTest {

  private lateinit var commandFactory: CommandFactory
  private val createdActions = mutableMapOf<KClass<out ProtocolAction>, ProtocolAction>()

  @Before
  fun setUp() {
    // Create a resolver that returns mock instances for each action class
    val resolver: (KClass<out ProtocolAction>) -> ProtocolAction = { kClass ->
      createdActions.getOrPut(kClass) { mockk(relaxed = true) }
    }
    commandFactory = CommandFactoryImpl(resolver)
  }

  // region NowPlaying protocol mappings

  @Test
  fun `create should return UpdateNowPlayingTrack for NowPlayingTrack protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingTrack)
    assertThat(createdActions.keys).contains(UpdateNowPlayingTrack::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateCover for NowPlayingCover protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingCover)
    assertThat(createdActions.keys).contains(UpdateCover::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateRating for NowPlayingRating protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingRating)
    assertThat(createdActions.keys).contains(UpdateRating::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateLyrics for NowPlayingLyrics protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingLyrics)
    assertThat(createdActions.keys).contains(UpdateLyrics::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateLfmRating for NowPlayingLfmRating protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingLfmRating)
    assertThat(createdActions.keys).contains(UpdateLfmRating::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateNowPlayingDetails for NowPlayingDetails protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingDetails)
    assertThat(createdActions.keys).contains(UpdateNowPlayingDetails::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateNowPlayingTrackRemoval for NowPlayingListRemove protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingListRemove)
    assertThat(createdActions.keys).contains(UpdateNowPlayingTrackRemoval::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateNowPlayingTrackMoved for NowPlayingListMove protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingListMove)
    assertThat(createdActions.keys).contains(UpdateNowPlayingTrackMoved::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdatePlaybackPositionCommand for NowPlayingPosition protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingPosition)
    assertThat(createdActions.keys).contains(UpdatePlaybackPositionCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateNowPlayingList for NowPlayingListChanged protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingListChanged)
    assertThat(createdActions.keys).contains(UpdateNowPlayingList::class)
    assertThat(action).isNotNull()
  }

  // endregion

  // region Player status protocol mappings

  @Test
  fun `create should return UpdatePlayerStatus for PlayerStatus protocol`() {
    val action = commandFactory.create(Protocol.PlayerStatus)
    assertThat(createdActions.keys).contains(UpdatePlayerStatus::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdatePlayState for PlayerState protocol`() {
    val action = commandFactory.create(Protocol.PlayerState)
    assertThat(createdActions.keys).contains(UpdatePlayState::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateRepeat for PlayerRepeat protocol`() {
    val action = commandFactory.create(Protocol.PlayerRepeat)
    assertThat(createdActions.keys).contains(UpdateRepeat::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateVolume for PlayerVolume protocol`() {
    val action = commandFactory.create(Protocol.PlayerVolume)
    assertThat(createdActions.keys).contains(UpdateVolume::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateMute for PlayerMute protocol`() {
    val action = commandFactory.create(Protocol.PlayerMute)
    assertThat(createdActions.keys).contains(UpdateMute::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateShuffle for PlayerShuffle protocol`() {
    val action = commandFactory.create(Protocol.PlayerShuffle)
    assertThat(createdActions.keys).contains(UpdateShuffle::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdateLastFm for PlayerScrobble protocol`() {
    val action = commandFactory.create(Protocol.PlayerScrobble)
    assertThat(createdActions.keys).contains(UpdateLastFm::class)
    assertThat(action).isNotNull()
  }

  // endregion

  // region Connection protocol mappings

  @Test
  fun `create should return ProtocolPingHandle for Ping protocol`() {
    val action = commandFactory.create(Protocol.Ping)
    assertThat(createdActions.keys).contains(ProtocolPingHandle::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return SimpleLogCommand for Pong protocol`() {
    val action = commandFactory.create(Protocol.Pong)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return ProtocolVersionUpdate for ProtocolTag protocol`() {
    val action = commandFactory.create(Protocol.ProtocolTag)
    assertThat(createdActions.keys).contains(ProtocolVersionUpdate::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return UpdatePluginVersionCommand for PluginVersion protocol`() {
    val action = commandFactory.create(Protocol.PluginVersion)
    assertThat(createdActions.keys).contains(UpdatePluginVersionCommand::class)
    assertThat(action).isNotNull()
  }

  // endregion

  // region Playback control protocol mappings

  @Test
  fun `create should return SimpleLogCommand for PlayerNext protocol`() {
    val action = commandFactory.create(Protocol.PlayerNext)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return SimpleLogCommand for PlayerPrevious protocol`() {
    val action = commandFactory.create(Protocol.PlayerPrevious)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return SimpleLogCommand for PlayerPlayPause protocol`() {
    val action = commandFactory.create(Protocol.PlayerPlayPause)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return SimpleLogCommand for NowPlayingListPlay protocol`() {
    val action = commandFactory.create(Protocol.NowPlayingListPlay)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  @Test
  fun `create should return SimpleLogCommand for PlaylistPlay protocol`() {
    val action = commandFactory.create(Protocol.PlaylistPlay)
    assertThat(createdActions.keys).contains(SimpleLogCommand::class)
    assertThat(action).isNotNull()
  }

  // endregion

  // region Error handling

  @Test(expected = IllegalStateException::class)
  fun `create should throw error for unsupported Init protocol`() {
    commandFactory.create(Protocol.Init)
  }

  @Test(expected = IllegalStateException::class)
  fun `create should throw error for unsupported Player protocol`() {
    commandFactory.create(Protocol.Player)
  }

  @Test(expected = IllegalStateException::class)
  fun `create should throw error for unsupported ClientNotAllowed protocol`() {
    commandFactory.create(Protocol.ClientNotAllowed)
  }

  @Test(expected = IllegalStateException::class)
  fun `create should throw error for unsupported UnknownCommand protocol`() {
    commandFactory.create(Protocol.UnknownCommand)
  }

  @Test(expected = IllegalStateException::class)
  fun `create should throw error for unsupported CommandUnavailable protocol`() {
    commandFactory.create(Protocol.CommandUnavailable)
  }

  // endregion

  // region Caching behavior

  @Test
  fun `create should return same action instance for repeated calls with same protocol`() {
    val action1 = commandFactory.create(Protocol.NowPlayingTrack)
    val action2 = commandFactory.create(Protocol.NowPlayingTrack)
    assertThat(action1).isSameInstanceAs(action2)
  }

  @Test
  fun `create should return different action instances for different protocols`() {
    val action1 = commandFactory.create(Protocol.NowPlayingTrack)
    val action2 = commandFactory.create(Protocol.PlayerStatus)
    assertThat(action1).isNotSameInstanceAs(action2)
  }

  // endregion
}
