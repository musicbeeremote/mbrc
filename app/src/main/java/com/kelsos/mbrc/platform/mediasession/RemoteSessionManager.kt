package com.kelsos.mbrc.platform.mediasession

import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import com.kelsos.mbrc.common.utilities.RemoteUtils
import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.performUserAction
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.Protocol
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@OptIn(KoinApiExtension::class)
class RemoteSessionManager(
  context: Application,
  volumeProvider: RemoteVolumeProvider,
  private val userActionUseCase: UserActionUseCase,
  private val manager: AudioManager
) : AudioManager.OnAudioFocusChangeListener {
  private val mediaSession: MediaSessionCompat

  lateinit var handler: MediaIntentHandler
  private val focusLock = Any()
  private val attributes = audioAttributes()
  private val request = AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
    .setAudioAttributes(attributes)
    .setOnAudioFocusChangeListener(this)
    .setWillPauseWhenDucked(true)
    .build()

  init {
    val myEventReceiver = ComponentName(context.packageName, MediaButtonReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
    mediaButtonIntent.component = myEventReceiver
    val mediaPendingIntent = PendingIntent.getBroadcast(
      context.applicationContext, 0, mediaButtonIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    mediaSession = MediaSessionCompat(context, "Session").apply {
      setMediaButtonReceiver(mediaPendingIntent)
      setPlaybackToRemote(volumeProvider)
    }

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
    })
  }

  private fun onConnectionStatusChanged(status: ConnectionStatus) {
    if (status != ConnectionStatus.Off) {
      return
    }

    val playbackState = PlaybackStateCompat.Builder()
      .setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
      .build()

    with(mediaSession) {
      isActive = false
      setPlaybackState(playbackState)
    }

    abandonFocus()
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession.sessionToken

  private suspend fun metadataUpdate(track: PlayingTrack) {
    val bitmap = RemoteUtils.coverBitmap(track.coverUrl)

    val meta = MediaMetadataCompat.Builder().apply {
      putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.album)
      putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
      putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
      putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    }

    mediaSession.setMetadata(meta.build())
  }

  private fun updateState(state: PlayerState) {

    when (state) {
      PlayerState.Playing -> isGranted(AudioManagerCompat.requestAudioFocus(manager, request))
      else -> abandonFocus()
    }

    val playbackState = PlaybackStateCompat.Builder()
      .setActions(PLAYBACK_ACTIONS)
      .apply {
        when (state) {
          PlayerState.Playing -> {
            setState(PlaybackStateCompat.STATE_PLAYING, -1, 1f)
            mediaSession.isActive = true
          }
          PlayerState.Paused -> {
            setState(PlaybackStateCompat.STATE_PAUSED, -1, 0f)
            mediaSession.isActive = true
          }
          else -> {
            setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
            mediaSession.isActive = false
          }
        }
      }.build()

    mediaSession.setPlaybackState(playbackState)
  }

  private fun isGranted(result: Int): Boolean {
    synchronized(focusLock) {
      return when (result) {
        AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> true
        AudioManager.AUDIOFOCUS_REQUEST_FAILED,
        AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> false
        else -> false
      }
    }
  }

  private fun audioAttributes(): AudioAttributesCompat = AudioAttributesCompat.Builder()
    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
    .build()

  private fun abandonFocus(): Boolean {
    return isGranted(AudioManagerCompat.abandonAudioFocusRequest(manager, request))
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
