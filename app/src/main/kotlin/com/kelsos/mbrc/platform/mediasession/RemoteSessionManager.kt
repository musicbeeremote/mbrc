package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.PlayerState.State
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.RemoteUtils
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
        postAction(UserAction(Protocol.PlayerPlay, true))
      }

      override fun onPause() {
        postAction(UserAction(Protocol.PlayerPause, true))
      }

      override fun onSkipToNext() {
        postAction(UserAction(Protocol.PlayerNext, true))
      }

      override fun onSkipToPrevious() {
        postAction(UserAction(Protocol.PlayerPrevious, true))
      }

      override fun onStop() {
        postAction(UserAction(Protocol.PlayerStop, true))
      }

      override fun onSeekTo(pos: Long) {
        postAction(UserAction.create(Protocol.NowPlayingPosition, pos))
      }
    })
  }

  private fun onConnectionStatusChanged(@Connection.Status status: Int) {
    if (status == Connection.OFF) {
      val builder = PlaybackStateCompat.Builder()
      builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
      val playbackState = builder.build()
      mediaSession.isActive = false
      mediaSession.setPlaybackState(playbackState)
    }
  }

  private fun postAction(action: UserAction) {
    userActionUseCase.perform(action)
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession.sessionToken

  private fun metadataUpdate(track: PlayingTrack) {
    val bitmap = RemoteUtils.coverBitmapSync(track.coverUrl)

    val builder = MediaMetadataCompat.Builder()
      .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
      .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
      .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
      .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
      .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration)
    mediaSession.setMetadata(builder.build())
  }

  private fun updateState(@State state: String) {

    val playbackState = PlaybackStateCompat.Builder()
      .setActions(PLAYBACK_ACTIONS)
      .apply {
        when (state) {
          PlayerState.PLAYING -> {
            setState(
              PlaybackStateCompat.STATE_PLAYING, -1
              /**change.position**/, 1f
            )
            mediaSession.isActive = true
          }
          PlayerState.PAUSED -> {
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
