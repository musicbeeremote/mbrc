package com.kelsos.mbrc.data.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.CoverAvailable;
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

public class TrackState {
    private RemoteApi api;

    private BehaviorSubject<String> lyricsSubject;
	private BehaviorSubject<Float> ratingSubject;
	private BehaviorSubject<LfmStatus> lfmRatingSubject;

    public static final String LOVE = "Love";
    public static final String BAN = "Ban";
	public static final String NORMAL = "Normal";
    private float rating;
    private String title;
    private String artist;
    private String album;
    private String year;
    private String lyrics;
    private LfmStatus lfmRating;
    private Bitmap albumCover;
	private String path;

	@Inject
    public TrackState(RemoteApi api) {
        this.api = api;

        lyrics = "";
		rating = 0;
		lfmRating = LfmStatus.NORMAL;

		lyricsSubject = BehaviorSubject.create(lyrics);
		ratingSubject = BehaviorSubject.create(rating);
		lfmRatingSubject = BehaviorSubject.create(lfmRating);

        Observable<Message> trackChangeObservable = Events.Messages.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.TRACK_CHANGED));

		Observable<Message> socketConnectedObservable = Events.Messages.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.filter(msg -> msg.getType().equals(EventType.SOCKET_CONNECTED));

		Observable<Message> mergeObservable = Observable.merge(trackChangeObservable, socketConnectedObservable);

        mergeObservable.subscribe(msg -> requestTrackInfo());
        mergeObservable.subscribe(msg -> requestCover());
        mergeObservable.subscribe(msg -> requestLyrics());

        Events.Messages.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
                .filter(msg -> msg.getType().equals(Notification.LYRICS_CHANGED))
                .subscribe(msg -> requestLyrics(), Logger::LogThrowable);

		Events.Messages.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.immediate())
				.filter(msg -> msg.getType().equals(Notification.RATING_CHANGED))
				.flatMap(msg -> api.getTrackRating())
				.subscribe(resp -> setRating(resp.getRating()),
						Logger::LogThrowable);

		init();
    }

	private void init() {
		requestCover();
		requestLyrics();
		requestTrackInfo();
	}


	private void requestTrackInfo() {
		api.getTrackInfo()
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.immediate())
				.subscribe(this::setTrackInfo, Logger::LogThrowable);
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
        onLfmRatingChanged(lfmRating);
    }

	private void onLfmRatingChanged(LfmStatus lfmRating) {
		lfmRatingSubject.onNext(lfmRating);
	}

	public void setTrackInfo(TrackResponse response) {
        this.artist = response.getArtist();
        this.album = response.getAlbum();
        this.year = response.getYear();
        this.title = response.getTitle();
		this.path = response.getPath();
        Events.TrackInfoChangeNotification.onNext(new TrackInfoChange(artist, title, album, year, path));
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

    public Observable<String> getLyricsObservable() {
        return lyricsSubject.asObservable();
    }

	public Observable<Float> observeRating() {
		return ratingSubject.asObservable();
	}

    public void setRating(float rating) {
        this.rating = rating;
		ratingSubject.onNext(rating);
    }



}
