package com.kelsos.mbrc.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.data.model.TrackModel;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.requests.PositionRequest;
import com.kelsos.mbrc.rest.responses.TrackInfo;
import com.kelsos.mbrc.rest.responses.TrackPositionResponse;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.kelsos.mbrc.utilities.RemoteUtils;
import java.io.InputStream;
import retrofit.client.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class TrackController {
  private TrackModel model;
  private RemoteApi api;
  private MainThreadBus bus;

  @Inject TrackController(TrackModel model, RemoteApi api, MainThreadBus bus) {
    this.model = model;
    this.api = api;
    this.bus = bus;
    this.init();
  }

  private void init() {
    retrieveCover();
    retrieveTrackInfo();
  }

  public Bitmap getCover() {
    return model.getAlbumCover();
  }

  public void retrieveCover() {
    api.getTrackCover(RemoteUtils.getTimeStamp())
        .subscribeOn(Schedulers.io())
        .subscribe(this::createBitmap, Ln::d);
  }

  public void retrieveTrackInfo() {
    api.getTrackInfo().subscribe(model::setTrackInfo, Ln::d);
  }

  public TrackInfo getTrackInfo() {
    return model.getInfo();
  }

  public void retrieveLyrics() {
    api.getTrackLyrics().subscribe(lyricsResponse -> {
      model.setLyrics(lyricsResponse.getLyrics());
    }, Ln::d);
  }

  private void createBitmap(Response response) {
    try {
      final InputStream stream = response.getBody().in();
      Bitmap cover = BitmapFactory.decodeStream(stream);
      model.setAlbumCover(cover);
    } catch (Exception ex) {
      Ln.d("Exception while creating bitmap :: %s", ex.getMessage());
    }
  }

  public void getTrackRating() {
    api.getTrackRating().subscribeOn(Schedulers.io()).
        subscribe(ratingResponse ->  {
          model.setRating(ratingResponse.getRating());
        });
  }

  public Observable<TrackPositionResponse> changePosition(int position) {
    return api.updatePosition(new PositionRequest().setPosition(position))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io());
  }
}
