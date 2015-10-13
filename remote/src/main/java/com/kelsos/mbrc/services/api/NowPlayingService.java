package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.rest.requests.MoveRequest;
import com.kelsos.mbrc.rest.requests.NowPlayingQueueRequest;
import com.kelsos.mbrc.rest.requests.PlayPathRequest;
import com.kelsos.mbrc.rest.responses.PaginatedResponse;
import com.kelsos.mbrc.rest.responses.SuccessResponse;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Single;

public interface NowPlayingService {


  @PUT("/nowplaying/queue/")
  Single<SuccessResponse> nowplayingQueue(@Body NowPlayingQueueRequest body);

  @GET("/nowplaying")
  Single<PaginatedResponse<NowPlayingTrack>> getNowPlayingList(@Query("offset") int offset,
                                                               @Query("limit") int limit);


  @DELETE("/nowplaying/{id}")
  Single<SuccessResponse> nowPlayingRemoveTrack(@Path("id") int id);

  @PUT("/nowplaying/play")
  Single<SuccessResponse> nowPlayingPlayTrack(@Body PlayPathRequest body);

  @PUT("/nowplaying/move")
  Single<SuccessResponse> nowPlayingMoveTrack(@Body MoveRequest body);

}
