package com.kelsos.mbrc.services.api;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;
import com.kelsos.mbrc.rest.requests.PositionRequest;
import com.kelsos.mbrc.rest.requests.RatingRequest;
import com.kelsos.mbrc.rest.responses.SuccessResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Single;

public interface TrackService {
  @PUT("/track/rating")
  Single<SuccessResponse> updateRating(@Body RatingRequest body);

  @PUT("/track/position")
  Single<Position> updatePosition(@Body PositionRequest body);

  @GET("/track/rating")
  Single<Rating> getTrackRating();

  @GET("/track/position")
  Single<Position> getCurrentPosition();

  @GET("/track/lyrics")
  Single<Lyrics> getTrackLyrics();

  @GET("/track/cover")
  @Streaming
  Single<Bitmap> getTrackCover(@Query("t") String timestamp);

  @GET("/track")
  Single<TrackInfo> getTrackInfo();


}
