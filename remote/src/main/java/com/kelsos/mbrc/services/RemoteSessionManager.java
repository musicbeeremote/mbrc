package com.kelsos.mbrc.services;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.utilities.MediaButtonReceiver;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

@Singleton
public class RemoteSessionManager {
  private static final long PLAYBACK_ACTIONS = PlaybackStateCompat.ACTION_PAUSE
      | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
      | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_STOP;

  private MediaSessionCompat mMediaSession;

  @Inject
  public RemoteSessionManager(final Context context, final Bus bus, final AudioManager manager) {
    bus.register(this);
    ComponentName myEventReceiver = new ComponentName(context.getPackageName(),
        MediaButtonReceiver.class.getName());
    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
    mediaButtonIntent.setComponent(myEventReceiver);
    PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
        0, mediaButtonIntent, 0);

    mMediaSession = new MediaSessionCompat(context, "Session", myEventReceiver, mediaPendingIntent);
  }


  public MediaSessionCompat.Token getMediaSessionToken() {
    return mMediaSession.getSessionToken();
  }


  @Subscribe public void metadataUpdate(NotificationDataAvailable data) {
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

  @Subscribe public void updateState(PlayStateChange stateChange) {
    if (mMediaSession == null) {
      return;
    }

    PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
    builder.setActions(PLAYBACK_ACTIONS);
    switch (stateChange.getState()) {
      case Playing:
        builder.setState(PlaybackStateCompat.STATE_PLAYING, -1, 1);
        break;
      case Paused:
        builder.setState(PlaybackStateCompat.STATE_PAUSED, -1, 0);
        break;
      default:
        builder.setState(PlaybackStateCompat.STATE_STOPPED, -1, 0);
        break;
    }
    PlaybackStateCompat playbackState = builder.build();
    mMediaSession.setPlaybackState(playbackState);

    mMediaSession.setActive(stateChange.getState() != PlayState.Stopped
        || stateChange.getState() != PlayState.Undefined);
  }


}
