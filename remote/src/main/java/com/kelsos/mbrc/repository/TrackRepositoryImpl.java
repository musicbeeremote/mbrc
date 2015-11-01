package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.inject.Inject;
import com.kelsos.mbrc.cache.TrackCache;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackPositionInteractor;
import com.kelsos.mbrc.interactors.TrackRatingInteractor;
import com.kelsos.mbrc.services.api.TrackService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackRepositoryImpl implements TrackRepository {
  @Inject
  private TrackRatingInteractor trackRatingUserCase;
  @Inject
  private TrackCoverInteractor trackCoverUserCase;
  @Inject
  private TrackPositionInteractor trackPositionInteractor;
  @Inject
  private TrackCache cache;
  @Inject private TrackService trackService;

  @Override
  public Observable<TrackInfo> getTrackInfo(boolean reload) {
    final Observable<TrackInfo> infoObservable = trackService.getTrackInfo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(trackInfo -> {
          cache.setTrackinfo(trackInfo);
          return Observable.just(trackInfo);
        });

    return reload ? infoObservable : Observable.concat(Observable.just(cache.getTrackinfo()), infoObservable)
        .filter(trackInfo -> trackInfo != null)
        .first();
  }

  @Override
  public void setTrackInfo(TrackInfo trackInfo) {
    cache.setTrackinfo(trackInfo);
  }

  @Override
  public Observable<List<String>> getTrackLyrics(boolean reload) {

    final Observable<List<String>> remote = trackService.getTrackLyrics()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(lyrics -> {
          String trackLyrics = lyrics.getLyrics();

          trackLyrics = trackLyrics.replace("<p>", "\r\n")
              .replace("<br>", "\n")
              .replace("&lt;", "<")
              .replace("&gt;", ">")
              .replace("&quot;", "\"")
              .replace("&apos;", "'")
              .replace("&amp;", "&")
              .replace("<p>", "\r\n")
              .replace("<br>", "\n")
              .trim();

          final List<String> lyricsList = TextUtils.isEmpty(trackLyrics)
              ? Collections.emptyList()
              : new ArrayList<>(Arrays.asList(trackLyrics.split("\r\n")));

          cache.setLyrics(lyricsList);
          return Observable.just(lyricsList);
        });

    return reload
        ? remote
        : Observable.concat(Observable.just(cache.getLyrics()), remote)
        .filter(strings -> strings != null)
        .first();
  }

  @Override
  public Observable<Bitmap> getTrackCover() {
    if (cache.getCover() == null) {
      return trackCoverUserCase.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(bitmap -> {
            cache.setCover(bitmap);
            return Observable.just(bitmap);
          });
    } else {
      return Observable.just(cache.getCover());
    }
  }

  @Override
  public void setTrackCover(Bitmap cover) {
    cache.setCover(cover);
  }

  @Override
  public Observable<Position> getPosition() {
    if (cache.getPosition() == null) {
      return trackPositionInteractor.execute().subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(position -> {
            cache.setPosition(position);
            return Observable.just(position);
          });
    } else {
      return Observable.just(cache.getPosition());
    }
  }

  @Override
  public void setPosition(Position position) {
    cache.setPosition(position);
  }

  @Override
  public Observable<Rating> getRating() {
    if (cache.getRating() == null) {
      return trackRatingUserCase.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(rating -> {
            cache.setRating(rating);
            return Observable.just(rating);
          });
    } else {
      return Observable.just(cache.getRating());
    }
  }

  @Override
  public void setRating(Rating rating) {
    cache.setRating(rating);
  }

  @Override
  public void setLyrics(List<String> lyrics) {
    cache.setLyrics(lyrics);
  }
}
