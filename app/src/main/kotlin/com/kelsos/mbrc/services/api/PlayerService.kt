package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.dto.RepeatResponse
import com.kelsos.mbrc.dto.player.*
import com.kelsos.mbrc.dto.requests.ChangeStateRequest
import com.kelsos.mbrc.dto.requests.RepeatRequest
import com.kelsos.mbrc.dto.requests.ShuffleRequest
import com.kelsos.mbrc.dto.requests.VolumeRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import rx.Observable

interface PlayerService {
    @GET("/player/volume")
    fun getVolume(): Observable<Volume>

    @PUT("/player/volume")
    fun updateVolume(@Body body: VolumeRequest): Observable<Volume>

    @PUT("/player/shuffle")
    fun updateShuffleState(@Body body: ShuffleRequest): Observable<Shuffle>

    @PUT("/player/scrobble")
    fun updateScrobbleState(@Body body: ChangeStateRequest): Observable<BaseResponse>

    @PUT("/player/repeat")
    fun updateRepeatState(@Body body: RepeatRequest): Observable<RepeatResponse>

    @PUT("/player/mute")
    fun updateMuteState(@Body body: ChangeStateRequest): Observable<StatusResponse>

    @GET("/player/shuffle")
    fun getShuffleState(): Observable<Shuffle>

    @GET("/player/scrobble")
    fun getScrobbleState(): Observable<StatusResponse>

    @GET("/player/repeat")
    fun getRepeatMode(): Observable<Repeat>

    @GET("/player/playstate")
    fun getPlayState(): Observable<PlayState>

    @GET("/player/status")
    fun getPlayerStatus(): Observable<PlayerStatusResponse>

    @GET("/player/mute")
    fun getMuteState(): Observable<StatusResponse>

    @GET("/player/action")
    fun performPlayerAction(@Query("action") @PlayerAction.Action action: String): Observable<BaseResponse>

}
