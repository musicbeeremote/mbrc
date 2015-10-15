package com.kelsos.mbrc.services.api;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.dto.requests.PositionRequest;
import com.kelsos.mbrc.dto.requests.RatingRequest;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Observable;
import rx.Single;

public interface TrackService {
  @PUT("/track/rating")
  Single<BaseResponse> updateRating(@Body RatingRequest body);

  @PUT("/track/position")
  Single<Position> updatePosition(@Body PositionRequest body);

  @GET("/track/rating")
  Single<Rating> getTrackRating();

  @GET("/track/position")
  Observable<Position> getCurrentPosition();

  @GET("/track/lyrics")
  Single<Lyrics> getTrackLyrics();

  @GET("/track/cover")
  @Streaming
  Single<Bitmap> getTrackCover(@Query("t") String timestamp);

  @GET("/track")
  Single<TrackInfo> getTrackInfo();


}
