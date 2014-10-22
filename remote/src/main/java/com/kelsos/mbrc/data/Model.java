package com.kelsos.mbrc.data;

import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;
import com.squareup.otto.Produce;

@Singleton
public class Model {

    private static final Logger logger = LoggerManager.getLogger();

    public static final int MAX_VOLUME = 100;
    public static final String LOVE = "Love";
    public static final String BAN = "Ban";
    public static final String ALL = "All";
    public static final String EMPTY = "";
    private MainThreadBusWrapper bus;
    private float rating;
    private String title;
    private String artist;
    private String album;
    private String year;
    private String lyrics;
    private int volume;
    private boolean isConnectionOn;
    private boolean repeatActive;
    private boolean shuffleActive;
    private boolean isScrobblingActive;
    private boolean isMuteActive;
    private PlayState playState;
    private LfmStatus lfmRating;
    private String pluginVersion;

    @Inject
    public Model(MainThreadBusWrapper bus, Context context) {
        this.bus = bus;
        bus.register(this);

        title = EMPTY;
        artist = EMPTY;
        album = EMPTY;
        year = EMPTY;
        volume = MAX_VOLUME;

        isConnectionOn = false;
        repeatActive = false;
        shuffleActive = false;
        isScrobblingActive = false;
        isMuteActive = false;
        playState = PlayState.STOPPED;
        rating = 0;
        lyrics = EMPTY;

        lfmRating = LfmStatus.NORMAL;
        pluginVersion = EMPTY;

    }

    public void setLfmRating(String rating) {
        switch (rating) {
            case LOVE:
                lfmRating = LfmStatus.LOVED;
                break;
            case BAN:
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
            bus.post(new NotificationDataAvailable(artist, title, album, playState));
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

    public void setConnectionState(String connectionActive) {
        isConnectionOn = Boolean.parseBoolean(connectionActive);
        if (!isConnectionOn) {
            setPlayState(Const.STOPPED);
        }
        bus.post(new ConnectionStatusChange(isConnectionOn
                ? ConnectionStatus.CONNECTION_ACTIVE
                : ConnectionStatus.CONNECTION_OFF));
    }

    @Produce public ConnectionStatusChange produceConnectionStatus() {
        return new ConnectionStatusChange(isConnectionOn
                    ? ConnectionStatus.CONNECTION_ACTIVE
                : ConnectionStatus.CONNECTION_OFF);
    }

    public boolean getIsConnectionActive() {
        return isConnectionOn;
    }

    public void setRepeatState(String repeatButtonActive) {
        repeatActive = (repeatButtonActive.equals(ALL));
        bus.post(new RepeatChange(this.isRepeatActive()));
    }

    @Produce public RepeatChange produceRepeatChange() {
        return new RepeatChange(this.isRepeatActive());
    }

    public void setShuffleState(boolean shuffleButtonActive) {
        shuffleActive = shuffleButtonActive;
        bus.post(new ShuffleChange(isShuffleActive()));
    }

    @Produce public ShuffleChange produceShuffleChange() {
        return new ShuffleChange(this.isShuffleActive());
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

    public boolean isRepeatActive() {
        return repeatActive;
    }

    public boolean isShuffleActive() {
        return shuffleActive;
    }
}

