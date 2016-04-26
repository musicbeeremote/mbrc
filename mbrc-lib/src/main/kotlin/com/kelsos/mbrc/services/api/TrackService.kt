package com.kelsos.mbrc.services.api

import android.graphics.Bitmap
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.dto.requests.PositionRequest
import com.kelsos.mbrc.dto.requests.RatingRequest
import com.kelsos.mbrc.dto.track.Lyrics
import com.kelsos.mbrc.dto.track.Position
import com.kelsos.mbrc.dto.track.Rating
import com.kelsos.mbrc.dto.track.TrackInfoResponse
import com.kelsos.mbrc.utilities.RemoteUtils
import retrofit2.http.*
import rx.Observable

interface TrackService {
    @PUT("/track/rating")
    fun updateRating(@Body body: RatingRequest): Observable<BaseResponse>

    @PUT("/track/position")
    fun updatePosition(@Body body: PositionRequest): Observable<Position>

    @GET("/track/rating")
    fun getTrackRating(): Observable<Rating>

    @GET("/track/position")
    fun getCurrentPosition(): Observable<Position>

    @GET("/track/lyrics")
    fun getTrackLyrics(): Observable<Lyrics>

    @GET("/track/cover")
    @Streaming
    fun getTrackCover(@Query("t") timestamp: String = RemoteUtils.timeStamp): Observable<Bitmap>

    @GET("/track")
    fun getTrackInfo(): Observable<TrackInfoResponse>


}
