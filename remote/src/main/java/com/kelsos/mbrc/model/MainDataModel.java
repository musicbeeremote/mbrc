package com.kelsos.mbrc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.AlbumEntry;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.data.GenreEntry;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.data.TrackEntry;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.AlbumSearchResults;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.events.ui.ConnectionStatusChange;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.GenreSearchResults;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.LyricsUpdated;
import com.kelsos.mbrc.events.ui.NotificationDataAvailable;
import com.kelsos.mbrc.events.ui.NowPlayingListAvailable;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RatingChanged;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.ScrobbleChange;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.events.ui.TrackSearchResults;
import com.kelsos.mbrc.events.ui.VolumeChange;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Produce;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.events.ui.ShuffleChange.OFF;
import static com.kelsos.mbrc.events.ui.ShuffleChange.ShuffleState;

@Singleton public class MainDataModel {

  private MainThreadBusWrapper bus;
  private Context context;
  private float rating;
  private String title;
  private String artist;
  private String album;
  private String year;
  private String lyrics;
  private int volume;
  private Bitmap cover;
  private boolean isConnectionOn;
  private boolean isHandShakeDone;
  private boolean isRepeatActive;
  private String mShuffleState;
  private boolean isScrobblingActive;
  private boolean isMuteActive;
  private PlayState playState;
  private ArrayList<TrackEntry> searchTracks;
  private ArrayList<AlbumEntry> searchAlbums;
  private ArrayList<GenreEntry> searchGenres;
  private ArrayList<ArtistEntry> searchArtists;
  private ArrayList<MusicTrack> nowPlayingList;
  private LfmStatus lfmRating;
  private String pluginVersion;

  @Inject public MainDataModel(MainThreadBusWrapper bus, Context context) {
    this.context = context;
    this.bus = bus;
    bus.register(this);

    title = artist = album = year = Const.EMPTY;
    volume = 100;

    isConnectionOn = false;
    isHandShakeDone = false;
    isRepeatActive = false;
    mShuffleState = OFF;
    isScrobblingActive = false;
    isMuteActive = false;
    playState = PlayState.Stopped;
    cover = null;
    rating = 0;
    lyrics = Const.EMPTY;

    searchArtists = new ArrayList<>();
    searchAlbums = new ArrayList<>();
    searchGenres = new ArrayList<>();
    searchTracks = new ArrayList<>();
    nowPlayingList = new ArrayList<>();
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

  @Produce public LfmRatingChanged produceLfmRating() {
    return new LfmRatingChanged(lfmRating);
  }

  public void setNowPlayingList(ArrayList<MusicTrack> nowPlayingList) {
    this.nowPlayingList = nowPlayingList;
    bus.post(new NowPlayingListAvailable(nowPlayingList,
        nowPlayingList.indexOf(new MusicTrack(artist, title))));
  }

  @Produce public NowPlayingListAvailable produceNowPlayingListAvailable() {
    int index = nowPlayingList.indexOf(new MusicTrack(artist, title));
    return new NowPlayingListAvailable(nowPlayingList, index);
  }

  public void setSearchArtists(ArrayList<ArtistEntry> searchArtists) {
    this.searchArtists = searchArtists;
    bus.post(new ArtistSearchResults(this.searchArtists, false));
  }

  @Produce public ArtistSearchResults produceArtistSearchResults() {
    return new ArtistSearchResults(searchArtists, true);
  }

  public void setSearchTracks(ArrayList<TrackEntry> searchTracks) {
    this.searchTracks = searchTracks;
    bus.post(new TrackSearchResults(searchTracks, false));
  }

  @Produce public TrackSearchResults produceTrackSearchResults() {
    return new TrackSearchResults(searchTracks, true);
  }

  public void setSearchAlbums(ArrayList<AlbumEntry> searchAlbums) {
    this.searchAlbums = searchAlbums;
    bus.post(new AlbumSearchResults(searchAlbums, false));
  }

  @Produce public AlbumSearchResults produceAlbumSearchResults() {
    return new AlbumSearchResults(searchAlbums, true);
  }

  public void setSearchGenres(ArrayList<GenreEntry> searchGenres) {
    this.searchGenres = searchGenres;
    bus.post(new GenreSearchResults(searchGenres, false));
  }

  @Produce public GenreSearchResults produceGenreSearchResults() {
    return new GenreSearchResults(searchGenres, true);
  }

  public void setRating(double rating) {
    this.rating = (float) rating;
    bus.post(new RatingChanged(this.rating));
  }

  @Produce public RatingChanged produceRatingChanged() {
    return new RatingChanged(this.rating);
  }

  private void updateNotification() {
    if (!isConnectionOn) {
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
    bus.post(new TrackInfoChange(artist, title, album, year));
    updateNotification();
  }

  @Produce public TrackInfoChange produceTrackInfo() {
    return new TrackInfoChange(artist, title, album, year);
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
      bus.post(new CoverAvailable());
      updateNotification();
    } else {
      Observable.create((Subscriber<? super Bitmap> subscriber) -> {
        byte[] decodedImage = Base64.decode(base64format, Base64.DEFAULT);
        subscriber.onNext(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length));
        subscriber.onCompleted();
      }).subscribeOn(Schedulers.io())
      .subscribe(this::setAlbumCover, throwable -> {
        cover = null;
        bus.post(new CoverAvailable());
      });
    }
  }

  public void setAlbumCover(Bitmap cover) {
    this.cover = cover;
    bus.post(new CoverAvailable(cover));
    updateNotification();
  }

  @Produce public CoverAvailable produceAvailableCover() {
    return cover == null ? new CoverAvailable() : new CoverAvailable(cover);
  }

  public void setConnectionState(String connectionActive) {
    isConnectionOn = Boolean.parseBoolean(connectionActive);
    if (!isConnectionOn) {
      setPlayState(Const.STOPPED);
    }
    bus.post(new ConnectionStatusChange(
        isConnectionOn ? (isHandShakeDone ? ConnectionStatus.CONNECTION_ACTIVE
            : ConnectionStatus.CONNECTION_ON) : ConnectionStatus.CONNECTION_OFF));
  }

  public void setHandShakeDone(boolean handShakeDone) {
    this.isHandShakeDone = handShakeDone;
    bus.post(new ConnectionStatusChange(
        isConnectionOn ? (isHandShakeDone ? ConnectionStatus.CONNECTION_ACTIVE
            : ConnectionStatus.CONNECTION_ON) : ConnectionStatus.CONNECTION_OFF));
  }

  @Produce public ConnectionStatusChange produceConnectionStatus() {
    return new ConnectionStatusChange(
        isConnectionOn ? (isHandShakeDone ? ConnectionStatus.CONNECTION_ACTIVE
            : ConnectionStatus.CONNECTION_ON) : ConnectionStatus.CONNECTION_OFF);
  }

  public boolean getIsConnectionActive() {
    return isConnectionOn;
  }

  public void setRepeatState(String repeatButtonActive) {
    isRepeatActive = (Protocol.ALL.equals(repeatButtonActive));
    bus.post(new RepeatChange(this.isRepeatActive));
  }

  @Produce public RepeatChange produceRepeatChange() {
    return new RepeatChange(this.isRepeatActive);
  }

  public void setShuffleState(@ShuffleState String shuffleState) {
    mShuffleState = shuffleState;
    bus.post(new ShuffleChange(mShuffleState));
  }

  @Produce public ShuffleChange produceShuffleChange() {
    return new ShuffleChange(this.mShuffleState);
  }

  public void setScrobbleState(boolean scrobbleButtonActive) {
    isScrobblingActive = scrobbleButtonActive;
    bus.post(new ScrobbleChange(isScrobblingActive));
  }

  @Produce public ScrobbleChange produceScrobbleChange() {
    return new ScrobbleChange(this.isScrobblingActive);
  }

  public void setMuteState(boolean isMuteActive) {
    this.isMuteActive = isMuteActive;
    bus.post(isMuteActive ? new VolumeChange() : new VolumeChange(volume));
  }

  @Produce public VolumeChange produceVolumeChange() {
    return isMuteActive ? new VolumeChange() : new VolumeChange(volume);
  }

  public void setPlayState(String playState) {
    PlayState newState = PlayState.Undefined;
    if (Const.PLAYING.equalsIgnoreCase(playState)) {
      newState = PlayState.Playing;
    } else if (Const.STOPPED.equalsIgnoreCase(playState)) {
      newState = PlayState.Stopped;
    } else if (Const.PAUSED.equalsIgnoreCase(playState)) newState = PlayState.Paused;
    this.playState = newState;
    bus.post(new PlayStateChange(this.playState));
    updateNotification();
  }

  @Produce public PlayStateChange producePlayState() {
    return new PlayStateChange(this.playState);
  }

  @Produce public LyricsUpdated produceLyricsUpdate() {
    return new LyricsUpdated(lyrics);
  }

  public void setLyrics(String lyrics) {
    if (lyrics == null || this.lyrics.equals(lyrics)) return;
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
}

