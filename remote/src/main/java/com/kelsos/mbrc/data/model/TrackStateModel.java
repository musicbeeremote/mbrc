package com.kelsos.mbrc.data.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.ui.*;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.util.Logger;
import com.kelsos.mbrc.util.RemoteUtils;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

import java.io.InputStream;

public class TrackStateModel {
    private RemoteApi api;

    public static final String LOVE = "Love";
    public static final String BAN = "Ban";
    private float rating;
    String title;
    String artist;
    String album;
    String year;
    String lyrics;
    LfmStatus lfmRating;
    Bitmap albumCover;

    @Inject
    public TrackStateModel(RemoteApi api) {
        this.api = api;
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

    public void requestCover() {
        api.getTrackCoverData(RemoteUtils.getTimeStamp())
                .subscribeOn(Schedulers.io())
                .subscribe(this::createBitmap, Logger::ProcessThrowable);
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

    public void setTrackInfo(String artist, String album, String title, String year) {
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.title = title;
        Events.TrackInfoChangeNotification.onNext(new TrackInfoChange(artist, title, album, year));
    }

    public String getArtist() {
        return this.artist;
    }

    public String getTitle() {
        return this.title;
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

    public void setRating(double rating) {
        this.rating = (float) rating;
        new RatingChanged(this.rating);
    }

}