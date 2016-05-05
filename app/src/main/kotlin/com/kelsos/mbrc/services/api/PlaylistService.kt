package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.constants.Constants.LIMIT
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.dto.PageResponse
import com.kelsos.mbrc.dto.playlist.PlaylistDto
import com.kelsos.mbrc.dto.playlist.PlaylistTrack
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import com.kelsos.mbrc.dto.requests.MoveRequest
import com.kelsos.mbrc.dto.requests.PlayPathRequest
import com.kelsos.mbrc.dto.requests.PlaylistRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Observable

interface PlaylistService {

  @PUT("/playlists/play")
  fun playPlaylist(@Body body: PlayPathRequest): Observable<BaseResponse>

  @PUT("/playlists/{id}/tracks/move")
  fun playlistMoveTrack(@Path("id") id: Int, @Body body: MoveRequest): Observable<BaseResponse>

  @GET("/playlists/{id}/tracks")
  fun getPlaylistTracks(@Path("id") id: Long,
                        @Query("after") after: Long = 0,
                        @Query("offset") offset: Int = 0,
                        @Query("limit") limit: Int = LIMIT): Observable<PageResponse<PlaylistTrack>>

  @DELETE("/playlists/{id}/tracks")
  fun deletePlaylistTrack(@Path("id") id: Int, @Query("index") index: Int): Observable<BaseResponse>

  @DELETE("/playlists/{id}")
  fun deletePlaylist(@Path("id") id: Int): Observable<BaseResponse>

  @PUT("/playlists")
  fun createPlaylist(@Body body: PlaylistRequest): Observable<BaseResponse>

  @GET("/playlists")
  fun getPlaylists(@Query("after") after: Long = 0,
                   @Query("offset") offset: Int = 0,
                   @Query("limit") limit: Int = LIMIT): Observable<PageResponse<PlaylistDto>>

  @PUT("/playlists/{id}/tracks")
  fun addTracksToPlaylist(@Path("id") id: Int,
                          @Body body: PlaylistRequest): Observable<BaseResponse>

  @GET("/playlists/trackinfo")
  fun getPlaylistTrackInfo(@Query("after") after: Long = 0,
                           @Query("offset") offset: Int = 0,
                           @Query("limit") limit: Int = LIMIT): Observable<PageResponse<PlaylistTrackInfo>>
}
