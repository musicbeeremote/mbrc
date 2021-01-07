package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.performUserAction
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.Protocol
import timber.log.Timber

class RemoteSessionManager(
  context: Application,
  volumeProvider: RemoteVolumeProvider,
  private val userActionUseCase: UserActionUseCase
) : AudioManager.OnAudioFocusChangeListener {
  private val mediaSession: MediaSessionCompat

  lateinit var handler: MediaIntentHandler

  init {
    val myEventReceiver = ComponentName(context.packageName, MediaButtonReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
    mediaButtonIntent.component = myEventReceiver
    val mediaPendingIntent = PendingIntent.getBroadcast(
      context.applicationContext, 0, mediaButtonIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    mediaSession = MediaSessionCompat(context, "Session", myEventReceiver, mediaPendingIntent)
    mediaSession.setPlaybackToRemote(volumeProvider)
    mediaSession.setCallback(object : MediaSessionCompat.Callback() {
      override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        val success = handler.handleMediaIntent(mediaButtonEvent)
        return success || super.onMediaButtonEvent(mediaButtonEvent)
      }

      override fun onPlay() {
        userActionUseCase.performUserAction(Protocol.PlayerPlay, true)
      }

      override fun onPause() {
        userActionUseCase.performUserAction(Protocol.PlayerPause, true)
      }

      override fun onSkipToNext() {
        userActionUseCase.performUserAction(Protocol.PlayerNext, true)
      }

      override fun onSkipToPrevious() {
        userActionUseCase.performUserAction(Protocol.PlayerPrevious, true)
      }

      override fun onStop() {
        userActionUseCase.performUserAction(Protocol.PlayerStop, true)
      }

      override fun onSeekTo(pos: Long) {
        userActionUseCase.performUserAction(Protocol.NowPlayingPosition, pos)
      }
    })
  }

  private fun onConnectionStatusChanged(status: ConnectionStatus) {
    if (status == ConnectionStatus.Off) {
      val builder = PlaybackStateCompat.Builder()
      builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
      val playbackState = builder.build()
      mediaSession.isActive = false
      mediaSession.setPlaybackState(playbackState)
    }
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession.sessionToken

  private suspend fun metadataUpdate(track: PlayingTrack) {
    val bitmap = RemoteUtils.coverBitmap(track.coverUrl)

    val builder = MediaMetadataCompat.Builder()
      .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
      .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
      .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
      .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
      .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
    mediaSession.setMetadata(builder.build())
  }

  private fun updateState(state: PlayerState) {
    val playbackState = PlaybackStateCompat.Builder()
      .setActions(PLAYBACK_ACTIONS)
      .apply {
        when (state) {
          PlayerState.Playing -> {
            setState(
              PlaybackStateCompat.STATE_PLAYING, -1
              /**change.position**/, 1f
            )
            mediaSession.isActive = true
          }
          PlayerState.Paused -> {
            setState(
              PlaybackStateCompat.STATE_PAUSED, -1
              /**change.position**/, 0f
            )
            mediaSession.isActive = true
          }
          else -> {
            setState(
              PlaybackStateCompat.STATE_STOPPED, -1
              /**change.position**/, 0f
            )
            mediaSession.isActive = false
          }
        }
      }.build()
    mediaSession.setPlaybackState(playbackState)
  }

  override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> Timber.d("gained")
      AudioManager.AUDIOFOCUS_LOSS,
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> Timber.d("transient loss")
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Timber.d("Loss can duck")
    }
  }

  companion object {
    private const val PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PAUSE or
      PlaybackStateCompat.ACTION_PLAY_PAUSE or
      PlaybackStateCompat.ACTION_PLAY or
      PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
      PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
      PlaybackStateCompat.ACTION_STOP or
      PlaybackStateCompat.ACTION_SEEK_TO
  }
}
