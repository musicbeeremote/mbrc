package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.dto.NowPlayingTrack
import com.kelsos.mbrc.dto.PaginatedResponse
import com.kelsos.mbrc.dto.requests.MoveRequest
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface NowPlayingService {

    @PUT("/nowplaying/queue/")
    fun nowplayingQueue(@Body body: NowPlayingQueueRequest): Observable<BaseResponse>

    @GET("/nowplaying")
    fun getNowPlayingList(@Query("offset") offset: Int,
                          @Query("limit") limit: Int): Observable<PaginatedResponse<NowPlayingTrack>>

    @DELETE("/nowplaying/{id}")
    fun nowPlayingRemoveTrack(@Path("id") id: Int): Observable<BaseResponse>

    @PUT("/nowplaying/play")
    fun nowPlayingPlayTrack(@Body body: PlayPathRequest): Observable<BaseResponse>

    @PUT("/nowplaying/move")
    fun nowPlayingMoveTrack(@Body body: MoveRequest): Observable<BaseResponse>
}
