package com.kelsos.mbrc.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.Connection;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.PlayerState.State;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Repeat.Mode;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.Playlist;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ConnectionStatusChangeEvent;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.events.ui.OnMainFragmentOptionsInflated;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.PlaylistAvailable;
import com.kelsos.mbrc.events.ui.RatingChanged;
import com.kelsos.mbrc.events.ui.RemoteClientMetaData;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ScrobbleChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.events.ui.VolumeChange;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.ui.ShuffleChange.OFF;
import static com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState;

@Singleton
public class MainDataModel {

  private RxBus bus;
  private float rating;
  private String title;
  private String artist;
  private String album;
  private String year;
  private String lyrics;
  private int volume;
  private Bitmap cover;
  private boolean connectionActive;
  private boolean isHandShakeDone;
  private String mShuffleState;
  private boolean isScrobblingActive;
  private boolean isMuteActive;
  @State
  private String playState;
  private LfmStatus lfmRating;
  private String pluginVersion;
  private double pluginProtocol;

  @Mode
  private String repeatMode;

  @Inject
  public MainDataModel(RxBus bus) {
    this.bus = bus;
    repeatMode = Repeat.NONE;

    title = artist = album = year = Const.EMPTY;
    volume = 100;

    connectionActive = false;
    isHandShakeDone = false;
    mShuffleState = OFF;
    isScrobblingActive = false;
    isMuteActive = false;
    playState = PlayerState.UNDEFINED;
    cover = null;
    rating = 0;
    lyrics = Const.EMPTY;

    lfmRating = LfmStatus.NORMAL;
    pluginVersion = Const.EMPTY;
  }

  public void setLfmRating(String rating) {
    switch (rating) {
      case "Love":
        lfmRating = LfmStatus.LOVED;
        break;
      case "Ban":
        lfmRating = LfmStatus.BANNED;
        break;
      default:
        lfmRating = LfmStatus.NORMAL;
        break;
    }

    bus.post(new LfmRatingChanged(lfmRating));
  }

  public String getPluginVersion() {
    return pluginVersion;
  }

  public void setPluginVersion(String pluginVersion) {
    this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
    bus.post(new MessageEvent(ProtocolEventType.PluginVersionCheck));
  }

  public void setNowPlayingList(ArrayList<MusicTrack> nowPlayingList) {
    bus.post(new NowPlayingListAvailable(nowPlayingList,
        nowPlayingList.indexOf(new MusicTrack(artist, title))));
  }

  public void setRating(double rating) {
    this.rating = (float) rating;
    bus.post(new RatingChanged(this.rating));
  }

  private void updateNotification() {
    if (!connectionActive) {
      bus.post(new MessageEvent(UserInputEventType.CancelNotification));
    } else {
      bus.post(new NotificationDataAvailable(artist, title, album, cover, playState));
    }
  }

  public void setTrackInfo(String artist, String album, String title, String year) {
    this.artist = artist;
    this.album = album;
    this.year = year;
    this.title = title;
    bus.post(new TrackInfo(artist, title, album, year));
    updateNotification();
    updateRemoteClient();
  }

  private void updateRemoteClient() {
    bus.post(new RemoteClientMetaData(artist, title, album, cover));
  }

  public String getArtist() {
    return this.artist;
  }

  public String getTitle() {
    return this.title;
  }

  public int getVolume() {
    return this.volume;
  }

  public void setVolume(int volume) {
    if (volume != this.volume) {
      this.volume = volume;
      bus.post(new VolumeChange(this.volume));
    }
  }

  public void setCover(final String base64format) {
    if (base64format == null || Const.EMPTY.equals(base64format)) {
      cover = null;
      bus.post(CoverChangedEvent.builder().build());
      updateNotification();
      updateRemoteClient();
    } else {
      Observable.create((Subscriber<? super Bitmap> subscriber) -> {
        byte[] decodedImage = Base64.decode(base64format, Base64.DEFAULT);
        subscriber.onNext(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
        subscriber.onCompleted();
      }).subscribeOn(Schedulers.io())
          .subscribe(this::setAlbumCover, throwable -> {
            cover = null;
            bus.post(CoverChangedEvent.builder().build());
          });
    }
  }

  private void setAlbumCover(Bitmap cover) {
    this.cover = cover;
    bus.post(CoverChangedEvent.builder().withCover(cover).build());
    updateNotification();
    updateRemoteClient();
  }

  public void setConnectionState(String connectionActive) {
    this.connectionActive = Boolean.parseBoolean(connectionActive);
    if (!this.connectionActive) {
      setPlayState(Const.STOPPED);
    }
    bus.post(new ConnectionStatusChangeEvent(
        this.connectionActive ? (isHandShakeDone ? Connection.ACTIVE
            : Connection.ON) : Connection.OFF));
  }

  public void setHandShakeDone(boolean handShakeDone) {
    this.isHandShakeDone = handShakeDone;
    bus.post(new ConnectionStatusChangeEvent(
        this.connectionActive ? (isHandShakeDone ? Connection.ACTIVE
            : Connection.ON) : Connection.OFF));
  }

  public boolean isConnectionActive() {
    return connectionActive;
  }

  public void setRepeatState(String repeat) {
    if (Protocol.ALL.equalsIgnoreCase(repeat)) {
      repeatMode = Repeat.ALL;
    } else if (Protocol.ONE.equalsIgnoreCase(repeat)) {
      repeatMode = Repeat.ONE;
    } else {
      repeatMode = Repeat.NONE;
    }

    bus.post(new RepeatChange(repeatMode));
  }

  public void setShuffleState(@ShuffleState String shuffleState) {
    mShuffleState = shuffleState;
    bus.post(new ShuffleChange(mShuffleState));
  }

  public void setScrobbleState(boolean scrobbleButtonActive) {
    isScrobblingActive = scrobbleButtonActive;
    bus.post(new ScrobbleChange(isScrobblingActive));
  }

  public void setMuteState(boolean isMuteActive) {
    this.isMuteActive = isMuteActive;
    bus.post(isMuteActive ? new VolumeChange() : new VolumeChange(volume));
  }

  public void setPlayState(String playState) {
    @State String newState;
    if (Const.PLAYING.equalsIgnoreCase(playState)) {
      newState = PlayerState.PLAYING;
    } else if (Const.STOPPED.equalsIgnoreCase(playState)) {
      newState = PlayerState.STOPPED;
    } else if (Const.PAUSED.equalsIgnoreCase(playState)) {
      newState = PlayerState.PAUSED;
    } else {
      newState = PlayerState.UNDEFINED;
    }

    this.playState = newState;

    bus.post(PlayStateChange.builder().state(this.playState).build());
    updateNotification();
  }

  public void setLyrics(String lyrics) {
    if (lyrics == null || this.lyrics.equals(lyrics)) {
      return;
    }
    this.lyrics = lyrics.replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .trim();
    bus.post(new LyricsUpdated(this.lyrics));
  }

  public boolean isMute() {
    return isMuteActive;
  }

  public void setPluginProtocol(double pluginProtocol) {
    this.pluginProtocol = pluginProtocol;
  }

  public double getPluginProtocol() {
    return this.pluginProtocol;
  }


  public void resendOnInflate(OnMainFragmentOptionsInflated inflated) {
    bus.post(new ScrobbleChange(isScrobblingActive));
    bus.post(new LfmRatingChanged(lfmRating));
  }

  public void setPlaylists(List<Playlist> playlists) {
    bus.post(PlaylistAvailable.create(playlists));
  }
}

