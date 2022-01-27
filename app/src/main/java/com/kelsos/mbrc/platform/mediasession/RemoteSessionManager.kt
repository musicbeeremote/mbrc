package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.state.models.Duration
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.perform
import com.kelsos.mbrc.networking.protocol.Protocol
import timber.log.Timber

private data class PlayState(
  val mediaSessionActive: Boolean,
  val position: Duration,
  val playbackSpeed: Float,
  val state: Int,
)

class RemoteSessionManager(
  context: Application,
  volumeProvider: RemoteVolumeProvider,
  private val userActionUseCase: UserActionUseCase,
) : AudioManager.OnAudioFocusChangeListener {
  private val mediaSession: MediaSessionCompat
  lateinit var handler: MediaIntentHandler

  init {
    val myEventReceiver = ComponentName(context.packageName, MediaButtonReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
    mediaButtonIntent.component = myEventReceiver
    val mediaPendingIntent =
      PendingIntent.getBroadcast(
        context.applicationContext,
        0,
        mediaButtonIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

    mediaSession = MediaSessionCompat(context, "Session", myEventReceiver, mediaPendingIntent)
    mediaSession.setPlaybackToRemote(volumeProvider)
    mediaSession.setCallback(
      object : MediaSessionCompat.Callback() {
        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
          val success = handler.handleMediaIntent(mediaButtonEvent)
          return success || super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onPlay() {
          userActionUseCase.perform(Protocol.PlayerPlay, true)
        }

        override fun onPause() {
          userActionUseCase.perform(Protocol.PlayerPause, true)
        }

        override fun onSkipToNext() {
          userActionUseCase.perform(Protocol.PlayerNext, true)
        }

        override fun onSkipToPrevious() {
          userActionUseCase.perform(Protocol.PlayerPrevious, true)
        }

        override fun onStop() {
          userActionUseCase.perform(Protocol.PlayerStop, true)
        }

        override fun onSeekTo(pos: Long) {
          userActionUseCase.perform(Protocol.NowPlayingPosition, pos)
        }
      },
    )
  }

  fun updateConnection(connected: Boolean) {
    if (!connected) {
      return
    }
    val builder = PlaybackStateCompat.Builder()
    builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
    val playbackState = builder.build()
    mediaSession.isActive = false
    mediaSession.setPlaybackState(playbackState)
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession.sessionToken

  fun updateTrack(
    track: PlayingTrack,
    cover: Bitmap?,
  ) {
    val builder =
      MediaMetadataCompat
        .Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, cover)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
    mediaSession.setMetadata(builder.build())
  }

  fun updateState(
    state: PlayerState,
    current: Duration,
  ) {
    val (mediaSessionActive, position, playbackSpeed, playbackState) =
      when (state) {
        PlayerState.Playing ->
          PlayState(
            mediaSessionActive = true,
            position = current,
            playbackSpeed = 1f,
            state = PlaybackStateCompat.STATE_PLAYING,
          )
        PlayerState.Paused ->
          PlayState(
            mediaSessionActive = false,
            position = current,
            playbackSpeed = 0f,
            state = PlaybackStateCompat.STATE_PAUSED,
          )
        else ->
          PlayState(
            mediaSessionActive = false,
            position = 0,
            playbackSpeed = 0f,
            state = PlaybackStateCompat.STATE_STOPPED, // To allow for notification dismissing
          )
      }
    mediaSession.isActive = mediaSessionActive
    mediaSession.setPlaybackState(
      PlaybackStateCompat
        .Builder()
        .setActions(PLAYBACK_ACTIONS)
        .setState(playbackState, position, playbackSpeed)
        .build(),
    )
  }

  override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> Timber.d("gained")
      AudioManager.AUDIOFOCUS_LOSS,
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
      -> Timber.d("transient loss")
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Timber.d("Loss can duck")
    }
  }

  companion object {
    private const val PLAYBACK_ACTIONS =
      PlaybackStateCompat.ACTION_PAUSE or
        PlaybackStateCompat.ACTION_PLAY_PAUSE or
        PlaybackStateCompat.ACTION_PLAY or
        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
        PlaybackStateCompat.ACTION_STOP or
        PlaybackStateCompat.ACTION_SEEK_TO
  }
}
