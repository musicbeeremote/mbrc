package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.dto.PaginatedResponse
import com.kelsos.mbrc.dto.playlist.PlaylistDto
import com.kelsos.mbrc.dto.playlist.PlaylistTrack
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import com.kelsos.mbrc.dto.requests.MoveRequest
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.dto.requests.PlaylistRequest
import retrofit2.http.*
import rx.Observable

interface PlaylistService {

    @PUT("/playlists/play")
    fun playPlaylist(@Body body: PlayPathRequest): Observable<BaseResponse>

    @PUT("/playlists/{id}/tracks/move")
    fun playlistMoveTrack(@Path("id") id: Int, @Body body: MoveRequest): Observable<BaseResponse>

    @GET("/playlists/{id}/tracks")
    fun getPlaylistTracks(@Path("id") id: Long, @Query("offset") offset: Int,
                          @Query("limit") limit: Int, @Query("after") after: Long): Observable<PaginatedResponse<PlaylistTrack>>

    @DELETE("/playlists/{id}/tracks")
    fun deletePlaylistTrack(@Path("id") id: Int, @Query("index") index: Int): Observable<BaseResponse>

    @DELETE("/playlists/{id}")
    fun deletePlaylist(@Path("id") id: Int): Observable<BaseResponse>

    @PUT("/playlists")
    fun createPlaylist(@Body body: PlaylistRequest): Observable<BaseResponse>

    @GET("/playlists")
    fun getPlaylists(@Query("offset") offset: Int, @Query("limit") limit: Int,
                     @Query("after") after: Long): Observable<PaginatedResponse<PlaylistDto>>

    @PUT("/playlists/{id}/tracks")
    fun addTracksToPlaylist(@Path("id") id: Int, @Body body: PlaylistRequest): Observable<BaseResponse>

    @GET("/playlists/trackinfo")
    fun getPlaylistTrackInfo(@Query("offset") offset: Int,
                             @Query("limit") limit: Int,
                             @Query("after") after: Long): Observable<PaginatedResponse<PlaylistTrackInfo>>
}
