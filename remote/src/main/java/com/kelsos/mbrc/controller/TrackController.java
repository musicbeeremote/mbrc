package com.kelsos.mbrc.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.data.model.TrackModel;
import com.kelsos.mbrc.events.ui.CoverAvailable;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.kelsos.mbrc.utilities.RemoteUtils;
import com.squareup.otto.Produce;
import java.io.InputStream;
import retrofit.client.Response;
import roboguice.util.Ln;
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
  }

  public void getCover() {
    api.getTrackCover(RemoteUtils.getTimeStamp())
        .subscribeOn(Schedulers.io())
        .subscribe(this::createBitmap, Ln::d);
  }

  private void createBitmap(Response response) {
    try {
      final InputStream stream = response.getBody().in();
      Bitmap cover = BitmapFactory.decodeStream(stream);
      model.setAlbumCover(cover);
      bus.post(new CoverAvailable(cover));
    } catch (Exception ex) {
      Ln.d("Exception while creating bitmap :: %s", ex.getMessage());
    }
  }

  @Produce public CoverAvailable produceCoverAvailable() {
    return new CoverAvailable(model.getAlbumCover());
  }
}
