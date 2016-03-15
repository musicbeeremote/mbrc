package com.kelsos.mbrc.services.api;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.requests.PositionRequest;
import com.kelsos.mbrc.dto.requests.RatingRequest;
import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfoResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

public interface TrackService {
  @PUT("/track/rating")
  Observable<BaseResponse> updateRating(@Body RatingRequest body);

  @PUT("/track/position")
  Observable<Position> updatePosition(@Body PositionRequest body);

  @GET("/track/rating")
  Observable<Rating> getTrackRating();

  @GET("/track/position")
  Observable<Position> getCurrentPosition();

  @GET("/track/lyrics")
  Observable<Lyrics> getTrackLyrics();

  @GET("/track/cover")
  @Streaming
  Observable<Bitmap> getTrackCover(@Query("t") String timestamp);

  @GET("/track")
  Observable<TrackInfoResponse> getTrackInfo();


}
