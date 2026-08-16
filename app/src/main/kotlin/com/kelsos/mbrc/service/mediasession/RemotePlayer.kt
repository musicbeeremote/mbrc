package com.kelsos.mbrc.service.mediasession

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.common.util.UnstableApi
import com.google.common.util.concurrent.Futures.immediateVoidFuture
import com.google.common.util.concurrent.ListenableFuture
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.ShuffleMode
import com.kelsos.mbrc.core.common.state.orEmpty
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.VolumeModifyUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.next
import com.kelsos.mbrc.core.networking.protocol.usecases.pause
import com.kelsos.mbrc.core.networking.protocol.usecases.performUserAction
import com.kelsos.mbrc.core.networking.protocol.usecases.play
import com.kelsos.mbrc.core.networking.protocol.usecases.previous
import com.kelsos.mbrc.core.platform.media.toMediaItem
import com.kelsos.mbrc.core.platform.state.toPlayingTrack
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(UnstableApi::class)
class RemotePlayer(
  context: Context,
  private val userActionUseCase: UserActionUseCase,
  private val volumeModifyUseCase: VolumeModifyUseCase,
  private val appState: AppStateFlow,
  private val dispatchers: AppCoroutineDispatchers,
  private val scope: CoroutineScope
) : SimpleBasePlayer(context.mainLooper) {
  init {
    appState.playerStatus.invalidateStateOnEach(scope)
    appState.playingPosition.invalidateStateOnEach(scope)
    appState.playingTrack.invalidateStateOnEach(scope)
  }

  private fun <T> StateFlow<T>.invalidateStateOnEach(scope: CoroutineScope) = onEach {
    withContext(dispatchers.main) { invalidateState() }
  }.launchIn(scope)

  /**
   * Runs a command off the main thread and answers Media3 immediately.
   *
   * Media3 dispatches these handlers on the application main looper, and every command ends in a
   * suspending write to the outgoing message queue. That queue parks the caller whenever nothing is
   * draining it, which is exactly the case while the connection is down or reconnecting, so waiting
   * for the command inline blocks the main thread until the system raises an ANR. Nothing here has
   * a result the caller needs, so the returned future completes right away and the command travels
   * on its own.
   */
  private fun dispatch(block: suspend () -> Unit): ListenableFuture<*> {
    scope.launch(dispatchers.network) { block() }
    return immediateVoidFuture()
  }

  private fun getPlaybackState(state: PlayerState): Int = when (state) {
    PlayerState.Playing -> STATE_READY
    PlayerState.Paused -> STATE_READY
    PlayerState.Undefined -> STATE_ENDED
    else -> STATE_IDLE
  }

  // Media3 calls this on the main thread and often, so it reads the state flows directly rather
  // than collecting them. Every value is already in memory; none of this touches the network.
  override fun getState(): State {
    val statusModel = appState.playerStatus.value
    val position = appState.playingPosition.value.orEmpty()
    val playingTrack = appState.playingTrack.value.orEmpty().toPlayingTrack()
    val isStream = position.isStream

    val commandsBuilder = Player.Commands
      .Builder()
      .add(COMMAND_PLAY_PAUSE)
      .add(COMMAND_STOP)
      .add(COMMAND_PREPARE)
      .add(COMMAND_SET_MEDIA_ITEM)
      .add(COMMAND_GET_CURRENT_MEDIA_ITEM)
      .add(COMMAND_RELEASE)
      .add(COMMAND_SET_SHUFFLE_MODE)
      .add(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
      .add(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
      .add(COMMAND_SEEK_TO_NEXT)
      .add(COMMAND_SEEK_TO_PREVIOUS)
      .add(COMMAND_GET_CURRENT_MEDIA_ITEM)
      .add(COMMAND_GET_METADATA)
      .add(COMMAND_GET_TIMELINE)
      .add(COMMAND_GET_DEVICE_VOLUME)
      .add(COMMAND_SET_DEVICE_VOLUME_WITH_FLAGS)
      .add(COMMAND_ADJUST_DEVICE_VOLUME_WITH_FLAGS)

    // Only add seek commands for non-stream content
    if (!isStream) {
      commandsBuilder
        .add(COMMAND_SEEK_BACK)
        .add(COMMAND_SEEK_FORWARD)
        .add(COMMAND_SEEK_TO_DEFAULT_POSITION)
        .add(COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
    }

    val commands = commandsBuilder.build()

    val item = playingTrack.toMediaItem()
    // For streams, set duration equal to current position so progress appears full
    val durationUs = if (!isStream) {
      position.total.toDuration(DurationUnit.MILLISECONDS).inWholeMicroseconds
    } else {
      position.current.toDuration(DurationUnit.MILLISECONDS).inWholeMicroseconds
    }
    val mediaItem = MediaItemData
      .Builder(0)
      .setMediaItem(item)
      .setMediaMetadata(item.mediaMetadata)
      .setIsSeekable(!isStream)
      .setDurationUs(durationUs)
      .build()

    val previous = MediaItemData.Builder("previous-track").build()
    val next = MediaItemData.Builder("next-track").build()

    val playlist = listOf(previous, mediaItem, next)

    return State
      .Builder()
      .setAvailableCommands(commands)
      .setAudioAttributes(AudioAttributes.DEFAULT)
      .setPlaybackState(getPlaybackState(statusModel.state))
      .setShuffleModeEnabled(statusModel.shuffle === ShuffleMode.Shuffle)
      .setPlayWhenReady(
        statusModel.state == PlayerState.Playing,
        PLAY_WHEN_READY_CHANGE_REASON_REMOTE
      )
      .setPlaylist(playlist)
      .setPlaylistMetadata(
        MediaMetadata
          .Builder()
          .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
          .setTitle("Now Playing")
          .build()
      ).setCurrentMediaItemIndex(1)
      .setContentPositionMs(position.current)
      .setIsDeviceMuted(statusModel.mute)
      .setDeviceVolume(statusModel.volume)
      .setDeviceInfo(
        DeviceInfo
          .Builder(DeviceInfo.PLAYBACK_TYPE_REMOTE)
          .setMinVolume(MIN_VOLUME)
          .setMaxVolume(MAX_VOLUME)
          .build()
      ).build()
  }

  override fun getPlaceholderMediaItemData(mediaItem: MediaItem): MediaItemData {
    val metadata =
      MediaMetadata
        .Builder()
        .setDisplayTitle("2")
        .setSubtitle("2")
        .setDescription("2")
        .build()
    return MediaItemData
      .Builder(0)
      .setMediaItem(mediaItem)
      .setMediaMetadata(metadata)
      .build()
  }

  override fun handlePrepare(): ListenableFuture<*> = immediateVoidFuture()

  override fun handleRelease(): ListenableFuture<*> = immediateVoidFuture()

  @SuppressLint("SwitchIntDef")
  override fun handleSeek(
    mediaItemIndex: Int,
    positionMs: Long,
    seekCommand: Int
  ): ListenableFuture<*> {
    Timber.d("received seek command: $seekCommand item: $mediaItemIndex at $positionMs")
    return dispatch {
      when (seekCommand) {
        COMMAND_SEEK_TO_PREVIOUS,
        COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
        -> userActionUseCase.previous()

        COMMAND_SEEK_TO_NEXT,
        COMMAND_SEEK_TO_NEXT_MEDIA_ITEM
        -> userActionUseCase.next()

        // Only a real seek carries a position. Skipping asks for the next track's default
        // position, which is MusicBee's to decide: forcing zero here would send a second command
        // for every skip and overwrite a resume position the player was holding.
        else -> {
          val to =
            when (positionMs) {
              C.TIME_UNSET -> 0L
              else -> positionMs
            }

          userActionUseCase.performUserAction(Protocol.NowPlayingPosition, to)
        }
      }
    }
  }

  override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {
    Timber.d("received play when ready: $playWhenReady")
    return dispatch {
      if (playWhenReady) {
        userActionUseCase.play()
      } else {
        userActionUseCase.pause()
      }
    }
  }

  override fun handleSetShuffleModeEnabled(shuffleModeEnabled: Boolean): ListenableFuture<*> {
    Timber.d("received shuffle mode enabled: $shuffleModeEnabled")
    return dispatch {
      val mode =
        if (shuffleModeEnabled) {
          ShuffleMode.OFF
        } else {
          ShuffleMode.SHUFFLE
        }
      userActionUseCase.perform(UserAction(Protocol.PlayerShuffle, mode))
    }
  }

  override fun handleStop(): ListenableFuture<*> = dispatch {
    userActionUseCase.perform(UserAction(Protocol.PlayerStop, true))
  }

  override fun handleSetDeviceVolume(deviceVolume: Int, flags: Int): ListenableFuture<*> {
    Timber.d("received device volume: $deviceVolume")
    return dispatch {
      userActionUseCase.perform(UserAction(Protocol.PlayerVolume, deviceVolume))
    }
  }

  override fun handleIncreaseDeviceVolume(flags: Int): ListenableFuture<*> {
    Timber.d("received increase device volume")
    return dispatch {
      volumeModifyUseCase.increase()
    }
  }

  override fun handleDecreaseDeviceVolume(flags: Int): ListenableFuture<*> {
    Timber.d("received decrease device volume")
    return dispatch {
      volumeModifyUseCase.decrease()
    }
  }

  override fun handleSetDeviceMuted(muted: Boolean, flags: Int): ListenableFuture<*> {
    Timber.d("received device muted: $muted")
    return dispatch {
      userActionUseCase.perform(UserAction(Protocol.PlayerMute, muted))
    }
  }

  override fun handleSetMediaItems(
    mediaItems: List<MediaItem>,
    startIndex: Int,
    startPositionMs: Long
  ): ListenableFuture<*> {
    Timber.d("received media items: $mediaItems")
    return immediateVoidFuture()
  }

  companion object {
    private const val MIN_VOLUME = 0
    private const val MAX_VOLUME = 100
  }
}
