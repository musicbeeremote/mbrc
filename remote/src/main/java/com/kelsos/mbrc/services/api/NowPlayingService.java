package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.dto.requests.MoveRequest;
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

public interface NowPlayingService {

  @PUT("/nowplaying/queue/")
  Observable<BaseResponse> nowplayingQueue(@Body NowPlayingQueueRequest body);

  @GET("/nowplaying")
  Observable<PaginatedResponse<NowPlayingTrack>> getNowPlayingList(@Query("offset") int offset,
      @Query("limit") int limit);

  @DELETE("/nowplaying/{id}")
  Single<BaseResponse> nowPlayingRemoveTrack(@Path("id") int id);

  @PUT("/nowplaying/play")
  Observable<BaseResponse> nowPlayingPlayTrack(@Body PlayPathRequest body);

  @PUT("/nowplaying/move")
  Single<BaseResponse> nowPlayingMoveTrack(@Body MoveRequest body);
}
