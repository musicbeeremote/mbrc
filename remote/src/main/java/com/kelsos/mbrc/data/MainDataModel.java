package com.kelsos.mbrc.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.dbdata.NowPlayingTrack;
import com.kelsos.mbrc.data.dbdata.Playlist;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.util.ImageDecoder;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class MainDataModel {

    public static final int MAX_VOLUME = 100;
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
    private boolean iShuffleActive;
    private boolean isScrobblingActive;
    private boolean isMuteActive;
    private PlayState playState;
    private List<NowPlayingTrack> nowPlayingList;
    private List<Playlist> availablePlaylists;
    private List<NowPlayingTrack> playlistTracks;
    private LfmStatus lfmRating;
    private String pluginVersion;

    @Inject
    public MainDataModel(MainThreadBusWrapper bus, Context context) {
        this.context = context;
        this.bus = bus;
        bus.register(this);

        title = "";
        artist = "";
        album = "";
        year = "";
        volume = MAX_VOLUME;

        isConnectionOn = false;
        isHandShakeDone = false;
        isRepeatActive = false;
        iShuffleActive = false;
        isScrobblingActive = false;
        isMuteActive = false;
        playState = PlayState.STOPPED;
        cover = null;
        rating = 0;
        lyrics = "";

        nowPlayingList = new ArrayList<>();
        availablePlaylists = new ArrayList<>();
        playlistTracks = new ArrayList<>();

        lfmRating = LfmStatus.NORMAL;
        pluginVersion = "";

    }

    public void setAvailablePlaylists(List<Playlist> playlists) {
        this.availablePlaylists = playlists;
        bus.post(new AvailablePlaylists(this.availablePlaylists, false));
    }

    public void setPlaylistTracks(List<NowPlayingTrack> tracks) {
        this.playlistTracks = tracks;
        bus.post(new PlaylistTracksAvailable(this.playlistTracks, false));
    }

    @Produce public PlaylistTracksAvailable producePlaylistTrack() {
        return new PlaylistTracksAvailable(this.playlistTracks, true);
    }

    @Produce public AvailablePlaylists availablePlaylistsChanged() {
        return new AvailablePlaylists(this.availablePlaylists, true);
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

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
        bus.post(new MessageEvent(ProtocolEventType.PLUGIN_VERSION_CHECK));
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    @Produce public LfmRatingChanged produceLfmRating() {
        return new LfmRatingChanged(lfmRating);
    }

    public void setNowPlayingList(List<NowPlayingTrack> nowPlayingList) {
        this.nowPlayingList = nowPlayingList;
        bus.post(new NowPlayingListAvailable(nowPlayingList, nowPlayingList.indexOf(new NowPlayingTrack(artist, title))));
    }

    @Produce public NowPlayingListAvailable produceNowPlayingListAvailable() {
        int index = nowPlayingList.indexOf(new NowPlayingTrack(artist, title));
        return new NowPlayingListAvailable(nowPlayingList, index);
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
            bus.post(new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION));
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
        } else {
            try {
                new ImageDecoder(context, base64format).execute();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(BuildConfig.PACKAGE_NAME, "image decoder", e);
                }
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
        bus.post(new ConnectionStatusChange(isConnectionOn
                ? (isHandShakeDone
                    ? ConnectionStatus.CONNECTION_ACTIVE
                    : ConnectionStatus.CONNECTION_ON)
                : ConnectionStatus.CONNECTION_OFF));
    }

    public void setHandShakeDone(boolean handShakeDone) {
        this.isHandShakeDone = handShakeDone;
        bus.post(new ConnectionStatusChange(isConnectionOn
                ? (isHandShakeDone
                    ? ConnectionStatus.CONNECTION_ACTIVE
                    : ConnectionStatus.CONNECTION_ON)
                : ConnectionStatus.CONNECTION_OFF));
    }

    @Produce public ConnectionStatusChange produceConnectionStatus() {
        return new ConnectionStatusChange(isConnectionOn
                ? (isHandShakeDone
                    ? ConnectionStatus.CONNECTION_ACTIVE
                    : ConnectionStatus.CONNECTION_ON)
                : ConnectionStatus.CONNECTION_OFF);
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

    public void setShuffleState(boolean shuffleButtonActive) {
        iShuffleActive = shuffleButtonActive;
        bus.post(new ShuffleChange(iShuffleActive));
    }

    @Produce public ShuffleChange produceShuffleChange() {
        return new ShuffleChange(this.iShuffleActive);
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
        PlayState newState = PlayState.UNDEFINED;
        if (playState.equalsIgnoreCase(Const.PLAYING)) {
            newState = PlayState.PLAYING;
        } else if (playState.equalsIgnoreCase(Const.STOPPED)) {
            newState = PlayState.STOPPED;
        } else if (playState.equalsIgnoreCase(Const.PAUSED)) {
            newState = PlayState.PAUSED;
        }
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
        if (lyrics == null || lyrics.equals(this.lyrics)) {
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
}

