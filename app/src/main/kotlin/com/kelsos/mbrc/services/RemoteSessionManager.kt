@file:Suppress("DEPRECATION")

package com.kelsos.mbrc.services

import android.annotation.TargetApi
import android.app.Application
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.AudioManager
import android.media.RemoteControlClient
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
import android.support.v4.media.session.PlaybackStateCompat
import com.kelsos.mbrc.annotations.Connection
import com.kelsos.mbrc.annotations.PlayerState
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.constants.ProtocolEventType
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.RemoteClientMetaData
import com.kelsos.mbrc.utilities.MediaButtonReceiver
import com.kelsos.mbrc.utilities.MediaIntentHandler
import com.kelsos.mbrc.utilities.RemoteUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSessionManager
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@Inject
constructor(
  context: Application,
  volumeProvider: RemoteVolumeProvider,
  private val bus: RxBus,
  private val manager: AudioManager
) : AudioManager.OnAudioFocusChangeListener {
  private val mediaSession: MediaSessionCompat?

  @Inject
  lateinit var handler: MediaIntentHandler

  init {

    bus.register(this, RemoteClientMetaData::class.java) { this.metadataUpdate(it) }
    bus.register(this, PlayStateChange::class.java) { this.updateState(it) }
    bus.register(this, PlayStateChange::class.java) { this.onPlayStateChange(it) }
    bus.register(
      this,
      ConnectionStatusChangeEvent::class.java
    ) { this.onConnectionStatusChanged(it) }

    val myEventReceiver = ComponentName(context.packageName, MediaButtonReceiver::class.java.name)
    val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
    mediaButtonIntent.component = myEventReceiver
    val mediaPendingIntent = PendingIntent.getBroadcast(
      context.applicationContext, 0, mediaButtonIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    mediaSession = MediaSessionCompat(context, "Session", myEventReceiver, mediaPendingIntent)
    mediaSession.setPlaybackToRemote(volumeProvider)
    mediaSession.setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS)
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
    })
  }

  private fun onConnectionStatusChanged(event: ConnectionStatusChangeEvent) {
    if (event.status == Connection.OFF) {
      if (mediaSession != null) {
        val builder = PlaybackStateCompat.Builder()
        builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
        val playbackState = builder.build()
        mediaSession.isActive = false
        mediaSession.setPlaybackState(playbackState)
        ensureTransportControls(playbackState)
      }
      abandonFocus()
    }
  }

  private fun postAction(action: UserAction) {
    bus.post(MessageEvent(ProtocolEventType.UserAction, action))
  }

  val mediaSessionToken: MediaSessionCompat.Token
    get() = mediaSession!!.sessionToken

  private fun metadataUpdate(data: RemoteClientMetaData) {
    if (mediaSession == null) {
      return
    }

    val trackInfo = data.trackInfo
    val bitmap = RemoteUtils.coverBitmapSync(data.coverPath)

    val builder = MediaMetadataCompat.Builder()
    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, trackInfo.album)
    builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, trackInfo.artist)
    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, trackInfo.title)
    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
    mediaSession.setMetadata(builder.build())
  }

  private fun updateState(stateChange: PlayStateChange) {
    if (mediaSession == null) {
      return
    }

    val builder = PlaybackStateCompat.Builder()
    builder.setActions(PLAYBACK_ACTIONS)
    when (stateChange.state) {
      PlayerState.PLAYING -> {
        builder.setState(PlaybackStateCompat.STATE_PLAYING, -1, 1f)
        mediaSession.isActive = true

      }
      PlayerState.PAUSED -> {
        builder.setState(PlaybackStateCompat.STATE_PAUSED, -1, 0f)
        mediaSession.isActive = true

      }
      else -> {
        builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0f)
        mediaSession.isActive = false
      }
    }
    val playbackState = builder.build()
    mediaSession.setPlaybackState(playbackState)
    ensureTransportControls(playbackState)
  }

  private fun onPlayStateChange(change: PlayStateChange) {
    when (change.state) {
      PlayerState.PLAYING -> {
        requestFocus()
      }
      else -> {
        abandonFocus()

      }
    }
  }

  private fun ensureTransportControls(playbackState: PlaybackStateCompat) {
    val actions = playbackState.actions
    val remoteObj = mediaSession!!.remoteControlClient
    if (actions != 0L && remoteObj != null) {

      var transportControls = 0

      if (actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
      }

      if (actions and PlaybackStateCompat.ACTION_REWIND != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_REWIND
      }

      if (actions and PlaybackStateCompat.ACTION_PLAY != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_PLAY
      }

      if (actions and PlaybackStateCompat.ACTION_PLAY_PAUSE != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
      }

      if (actions and PlaybackStateCompat.ACTION_PAUSE != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
      }

      if (actions and PlaybackStateCompat.ACTION_STOP != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_STOP
      }

      if (actions and PlaybackStateCompat.ACTION_FAST_FORWARD != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_FAST_FORWARD
      }

      if (actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
        transportControls = transportControls or RemoteControlClient.FLAG_KEY_MEDIA_NEXT
      }

      (remoteObj as RemoteControlClient).setTransportControlFlags(transportControls)
    }
  }

  private fun requestFocus(): Boolean {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.requestAudioFocus(
      this,
      AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
    )
  }

  private fun abandonFocus(): Boolean {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.abandonAudioFocus(this)
  }

  override fun onAudioFocusChange(focusChange: Int) {
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> Timber.d("gained")
      AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> Timber.d("transient loss")
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> Timber.d("Loss can duck")
    }
  }

  companion object {
    private const val PLAYBACK_ACTIONS =
      PlaybackStateCompat.ACTION_PAUSE or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or PlaybackStateCompat.ACTION_STOP
  }
}
