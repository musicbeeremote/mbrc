package com.kelsos.mbrc.services;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RemoteClientMetaData;
import com.kelsos.mbrc.utilities.MediaButtonReceiver;
import com.kelsos.mbrc.utilities.MediaIntentHandler;
import com.kelsos.mbrc.utilities.RxBus;
import roboguice.util.Ln;

@Singleton public class RemoteSessionManager implements AudioManager.OnAudioFocusChangeListener {
  private static final long PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PAUSE
      | PlaybackStateCompat.ACTION_PLAY_PAUSE
      | PlaybackStateCompat.ACTION_PLAY
      | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
      | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
      | PlaybackStateCompat.ACTION_STOP;
  private final AudioManager manager;
  private final RxBus bus;
  private MediaSessionCompat mMediaSession;
  @Inject private MediaIntentHandler handler;

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) @Inject
  public RemoteSessionManager(final Context context, final RxBus bus, final AudioManager manager) {
    this.manager = manager;
    this.bus = bus;

    bus.register(this, RemoteClientMetaData.class, this::metadataUpdate);
    bus.register(this, PlayStateChange.class, this::updateState);
    bus.register(this, PlayStateChange.class, this::onPlayStateChange);

    ComponentName myEventReceiver =
        new ComponentName(context.getPackageName(), MediaButtonReceiver.class.getName());
    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
    mediaButtonIntent.setComponent(myEventReceiver);
    PendingIntent mediaPendingIntent =
        PendingIntent.getBroadcast(context.getApplicationContext(), 0, mediaButtonIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    mMediaSession = new MediaSessionCompat(context, "Session", myEventReceiver, mediaPendingIntent);

    mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
        | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mMediaSession.setCallback(new MediaSessionCompat.Callback() {
        @Override public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
          boolean success = handler.handleMediaIntent(mediaButtonEvent);
          return success || super.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override public void onPlay() {

        }

        @Override public void onPause() {

        }

        @Override public void onSkipToNext() {

        }

        @Override public void onSkipToPrevious() {

        }

        @Override public void onStop() {

        }
      });
    }
  }

  public MediaSessionCompat.Token getMediaSessionToken() {
    return mMediaSession.getSessionToken();
  }

  public void metadataUpdate(RemoteClientMetaData data) {
    if (mMediaSession == null) {
      return;
    }


    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
    builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, data.getAlbum());
    builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, data.getArtist());
    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, data.getTitle());
    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, data.getCover());
    mMediaSession.setMetadata(builder.build());
  }

  public void updateState(PlayStateChange stateChange) {
    if (mMediaSession == null) {
      return;
    }

    PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
    builder.setActions(PLAYBACK_ACTIONS);
    final String playerState = stateChange.getState();
    switch (playerState) {
      case PlayerState.PLAYING:
        builder.setState(PlaybackStateCompat.STATE_PLAYING, -1, 1);
        break;
      case PlayerState.PAUSED:
        builder.setState(PlaybackStateCompat.STATE_PAUSED, -1, 0);
        break;
      default:
        builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0);
        break;
    }
    PlaybackStateCompat playbackState = builder.build();
    mMediaSession.setPlaybackState(playbackState);
    ensureTransportControls(playbackState);

    final boolean isActive = PlayerState.STOPPED.equals(playerState)
        || PlayerState.UNDEFINED.equals(playerState);
    mMediaSession.setActive(isActive);
  }

  public void onPlayStateChange(PlayStateChange change) {
    switch (change.getState()) {
      case PlayerState.PLAYING:
        requestFocus();
        break;
      case PlayerState.PAUSED:
        break;
      default:
        abandonFocus();
        break;
    }
  }

  @SuppressWarnings("deprecation") @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private void ensureTransportControls(PlaybackStateCompat playbackState) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH
        || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return;
    }

    long actions = playbackState.getActions();
    Object remoteObj = mMediaSession.getRemoteControlClient();
    if (actions != 0 && remoteObj != null) {

      int transportControls = 0;

      if ((actions & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS;
      }

      if ((actions & PlaybackStateCompat.ACTION_REWIND) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_REWIND;
      }

      if ((actions & PlaybackStateCompat.ACTION_PLAY) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_PLAY;
      }

      if ((actions & PlaybackStateCompat.ACTION_PLAY_PAUSE) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
      }

      if ((actions & PlaybackStateCompat.ACTION_PAUSE) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_PAUSE;
      }

      if ((actions & PlaybackStateCompat.ACTION_STOP) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_STOP;
      }

      if ((actions & PlaybackStateCompat.ACTION_FAST_FORWARD) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_FAST_FORWARD;
      }

      if ((actions & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0) {
        transportControls |= RemoteControlClient.FLAG_KEY_MEDIA_NEXT;
      }

      ((RemoteControlClient) remoteObj).setTransportControlFlags(transportControls);
    }
  }

  private boolean requestFocus() {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.requestAudioFocus(this,
        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
  }

  private boolean abandonFocus() {
    return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == manager.abandonAudioFocus(this);
  }

  public void onAudioFocusChange(int focusChange) {
    switch (focusChange) {
      case AudioManager.AUDIOFOCUS_GAIN:
        Ln.d("gained");
        break;
      case AudioManager.AUDIOFOCUS_LOSS:
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        Ln.d("transient loss");
        break;
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
        Ln.d("Loss can duck");
        break;
      default:
    }
  }
}
