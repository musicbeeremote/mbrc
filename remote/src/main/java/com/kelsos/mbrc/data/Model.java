package com.kelsos.mbrc.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.enums.ConnectionStatus;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.net.Notification;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.RemoteUtils;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

import java.io.InputStream;

@Singleton
public class Model {
    public static final int MAX_VOLUME = 100;
    public static final String LOVE = "Love";
    public static final String BAN = "Ban";
    public static final String ALL = "All";
    public static final String EMPTY = "";
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
    private Bitmap albumCover;
    private RemoteApi api;


    @Inject
    public Model(Context context, RemoteApi api) {
        this.api = api;

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


        Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.PLAY_STATUS_CHANGED))
                .subscribe(event -> Ln.d("PlayStatus changed"));

        Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.LYRICS_CHANGED))
                .flatMap(resp -> api.getTrackLyrics())
                .subscribe(resp -> setLyrics(resp.getLyrics()));

        Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.COVER_CHANGED))
                .subscribe(resp -> requestCover());

        Ln.d("Model instantiated");

    }

    private void createBitmap(Response response) {
        try {
            final InputStream stream = response.getBody().in();
            albumCover = BitmapFactory.decodeStream(stream);
            Events.CoverAvailableNotification.onNext(new CoverAvailable(albumCover));
        } catch (Exception ex) {
            Ln.d("Exception while creating bitmap :: %s", ex.getMessage());
        }
    }

    public void requestCover() {
        api.getTrackCoverData(RemoteUtils.getTimeStamp())
                .subscribeOn(Schedulers.io())
                .subscribe(this::createBitmap,
                        error -> Ln.d("Error will decoding stream :: %s", error.getMessage()));
    }

    public Bitmap getAlbumCover() {
        return albumCover;
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

        new LfmRatingChanged(lfmRating);
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion.substring(0, pluginVersion.lastIndexOf('.'));
    }

    public void setRating(double rating) {
        this.rating = (float) rating;
        new RatingChanged(this.rating);
    }

    private void updateNotification() {
        if (!isConnectionOn) {
            new MessageEvent(UserInputEventType.CANCEL_NOTIFICATION);
        } else {
            new NotificationDataAvailable(artist, title, album, playState);
        }
    }

    public void setTrackInfo(String artist, String album, String title, String year) {
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.title = title;
        new TrackInfoChange(artist, title, album, year);
        updateNotification();
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
            new VolumeChange(this.volume);
        }
    }

    public void setConnectionState(String connectionActive) {
        isConnectionOn = Boolean.parseBoolean(connectionActive);
        if (!isConnectionOn) {
            setPlayState(Const.STOPPED);
        }
        new ConnectionStatusChange(isConnectionOn
                ? ConnectionStatus.CONNECTION_ACTIVE
                : ConnectionStatus.CONNECTION_OFF);
    }

    public boolean getIsConnectionActive() {
        return isConnectionOn;
    }

    public void setRepeatState(String repeatButtonActive) {
        repeatActive = (repeatButtonActive.equals(ALL));
        new RepeatChange(this.isRepeatActive());
    }

    public void setShuffleState(boolean shuffleButtonActive) {
        shuffleActive = shuffleButtonActive;
        new ShuffleChange(isShuffleActive());
    }

    public void setScrobbleState(boolean scrobbleButtonActive) {
        isScrobblingActive = scrobbleButtonActive;
        new ScrobbleChange(isScrobblingActive);
    }

    public void setMuteState(boolean isMuteActive) {
        this.isMuteActive = isMuteActive;
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
        new PlayStateChange(this.playState);
        updateNotification();
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
        new LyricsUpdated(this.lyrics);
    }

    public boolean isRepeatActive() {
        return repeatActive;
    }

    public boolean isShuffleActive() {
        return shuffleActive;
    }

}

