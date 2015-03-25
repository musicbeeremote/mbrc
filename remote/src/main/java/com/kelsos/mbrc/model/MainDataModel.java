package com.kelsos.mbrc.model;

import android.content.Context;
import android.graphics.Bitmap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.*;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.utilities.ImageDecoder;
import com.squareup.otto.Produce;

import java.util.ArrayList;

import static com.kelsos.mbrc.events.ui.ShuffleChange.*;

@Singleton
public class MainDataModel {

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
    private boolean isAutoDjActive;
    private PlayState playState;
    private ArrayList<TrackEntry> searchTracks;
    private ArrayList<AlbumEntry> searchAlbums;
    private ArrayList<GenreEntry> searchGenres;
    private ArrayList<ArtistEntry> searchArtists;
    private ArrayList<MusicTrack> nowPlayingList;
    private LfmStatus lfmRating;
    private String pluginVersion;

    @Inject
    public MainDataModel(MainThreadBusWrapper bus, Context context) {
        this.context = context;
        this.bus = bus;
        bus.register(this);

        title = artist = album = year = "";
        volume = 100;

        isConnectionOn = false;
        isHandShakeDone = false;
        isRepeatActive = false;
        mShuffleState = OFF;
        isScrobblingActive = false;
        isMuteActive = false;
        isAutoDjActive = false;
        playState = PlayState.Stopped;
        cover = null;
        rating = 0;
        lyrics = "";

        searchArtists = new ArrayList<>();
        searchAlbums = new ArrayList<>();
        searchGenres = new ArrayList<>();
        searchTracks = new ArrayList<>();
        nowPlayingList = new ArrayList<>();
        lfmRating = LfmStatus.NORMAL;
        pluginVersion = "";

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

    public void setAutoDjActive(boolean isActive) {
        isAutoDjActive = isActive;
        bus.post(new AutoDjChange(isAutoDjActive));
    }

    @Produce public AutoDjChange produceAutoDjState() {
        return new AutoDjChange(isAutoDjActive);
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
        bus.post(new MessageEvent(ProtocolEventType.PluginVersionCheck));
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    @Produce public LfmRatingChanged produceLfmRating() {
        return new LfmRatingChanged(lfmRating);
    }

    public void setNowPlayingList(ArrayList<MusicTrack> nowPlayingList) {
        this.nowPlayingList = nowPlayingList;
        bus.post(new NowPlayingListAvailable(nowPlayingList, nowPlayingList.indexOf(new MusicTrack(artist, title))));
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

    public void setVolume(int volume) {
        if (volume != this.volume) {
            this.volume = volume;
            bus.post(new VolumeChange(this.volume));
        }
    }

    public int getVolume() {
        return this.volume;
    }

    public void setCover(String base64format) {
        if (base64format == null || base64format.equals("")) {
            cover = null;
            bus.post(new CoverAvailable());
            updateNotification();
        } else {
            try {
                new ImageDecoder(context, base64format).execute();
            } catch (Exception ignore) {

            }
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
        bus.post(new ConnectionStatusChange(isConnectionOn ?
                (isHandShakeDone ?
                        ConnectionStatus.CONNECTION_ACTIVE :
                        ConnectionStatus.CONNECTION_ON) :
                ConnectionStatus.CONNECTION_OFF));
    }

    public void setHandShakeDone(boolean handShakeDone) {
        this.isHandShakeDone = handShakeDone;
        bus.post(new ConnectionStatusChange(isConnectionOn ?
                (isHandShakeDone ?
                        ConnectionStatus.CONNECTION_ACTIVE :
                        ConnectionStatus.CONNECTION_ON) :
                ConnectionStatus.CONNECTION_OFF));
    }

    @Produce public ConnectionStatusChange produceConnectionStatus() {
        return new ConnectionStatusChange(isConnectionOn ?
                (isHandShakeDone ?
                        ConnectionStatus.CONNECTION_ACTIVE :
                        ConnectionStatus.CONNECTION_ON) :
                ConnectionStatus.CONNECTION_OFF);
    }

    public boolean getIsConnectionActive() {
        return isConnectionOn;
    }

    public void setRepeatState(String repeatButtonActive) {
        isRepeatActive = (repeatButtonActive.equals("All"));
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
        if (playState.equalsIgnoreCase(Const.PLAYING)) newState = PlayState.Playing;
        else if (playState.equalsIgnoreCase(Const.STOPPED)) newState = PlayState.Stopped;
        else if (playState.equalsIgnoreCase(Const.PAUSED)) newState = PlayState.Paused;
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
        if (lyrics != null && this.lyrics.equals(lyrics)) return;
        this.lyrics = lyrics.replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&").replace("<p>", "\r\n").replace("<br>", "\n").trim();
        bus.post(new LyricsUpdated(this.lyrics));
    }
}

