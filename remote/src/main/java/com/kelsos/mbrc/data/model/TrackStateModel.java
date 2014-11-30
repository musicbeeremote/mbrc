package com.kelsos.mbrc.data.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.events.ui.LfmRatingChanged;
import com.kelsos.mbrc.events.ui.RatingChanged;
import com.kelsos.mbrc.events.ui.TrackInfoChange;
import com.kelsos.mbrc.net.Notification;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.TrackResponse;
import com.kelsos.mbrc.util.Logger;
import com.kelsos.mbrc.util.RemoteUtils;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.io.InputStream;

public class TrackStateModel {
    private RemoteApi api;

    private BehaviorSubject<String> lyricsSubject;
    public static final String LOVE = "Love";
    public static final String BAN = "Ban";
    private float rating;
    private String title;
    private String artist;
    private String album;
    private String year;
    private String lyrics;
    private LfmStatus lfmRating;
    private Bitmap albumCover;

    @Inject
    public TrackStateModel(RemoteApi api) {
        this.api = api;
        lyrics = "";
        lyricsSubject = BehaviorSubject.create(lyrics);
        Observable<MessageEvent> trackChangeObservable = Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.TRACK_CHANGED));

        trackChangeObservable.flatMap(msg -> api.getTrackInfo())
                .subscribe(this::setTrackInfo, Logger::LogThrowable);

        trackChangeObservable.subscribe(msg -> requestCover());

        trackChangeObservable.subscribe(msg -> requestLyrics());

        Events.Messages.subscribeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.LYRICS_CHANGED))
                .subscribe(msg -> requestLyrics());

    }

    void createBitmap(Response response) {
        try {
            final InputStream stream = response.getBody().in();
            albumCover = BitmapFactory.decodeStream(stream);
            Events.CoverAvailableNotification.onNext(new CoverAvailable(albumCover));
        } catch (Exception ex) {
            Ln.d("Exception while creating bitmap :: %s", ex.getMessage());
        }
    }

    private void requestCover() {
        api.getTrackCoverData(RemoteUtils.getTimeStamp())
                .subscribeOn(Schedulers.io())
                .subscribe(this::createBitmap, Logger::LogThrowable);
    }

    private void requestLyrics() {
        api.getTrackLyrics().
                subscribeOn(Schedulers.io())
                .subscribe(resp -> setLyrics(resp.getLyrics()));
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

    public void setTrackInfo(TrackResponse response) {
        this.artist = response.getArtist();
        this.album = response.getAlbum();
        this.year = response.getYear();
        this.title = response.getTitle();
        Events.TrackInfoChangeNotification.onNext(new TrackInfoChange(artist, title, album, year));
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

        lyricsSubject.onNext(this.lyrics);
    }

    public Observable<String> getLyricsObservable(){
        return lyricsSubject.asObservable();
    }

    public void setRating(double rating) {
        this.rating = (float) rating;
        new RatingChanged(this.rating);
    }

}