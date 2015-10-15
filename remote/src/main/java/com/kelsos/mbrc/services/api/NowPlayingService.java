package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.dto.requests.MoveRequest;
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Single;

public interface NowPlayingService {


  @PUT("/nowplaying/queue/")
  Single<BaseResponse> nowplayingQueue(@Body NowPlayingQueueRequest body);

  @GET("/nowplaying")
  Single<PaginatedResponse<NowPlayingTrack>> getNowPlayingList(@Query("offset") int offset,
                                                               @Query("limit") int limit);


  @DELETE("/nowplaying/{id}")
  Single<BaseResponse> nowPlayingRemoveTrack(@Path("id") int id);

  @PUT("/nowplaying/play")
  Single<BaseResponse> nowPlayingPlayTrack(@Body PlayPathRequest body);

  @PUT("/nowplaying/move")
  Single<BaseResponse> nowPlayingMoveTrack(@Body MoveRequest body);

}
