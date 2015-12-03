package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.dto.playlist.PlaylistDto;
import com.kelsos.mbrc.dto.playlist.PlaylistTrack;
import com.kelsos.mbrc.dto.requests.MoveRequest;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;
import com.kelsos.mbrc.dto.requests.PlaylistRequest;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.Single;

public interface PlaylistService {

  @PUT("/playlists/play")
  Single<BaseResponse> playPlaylist(@Body PlayPathRequest body);

  @PUT("/playlists/{id}/tracks/move")
  Single<BaseResponse> playlistMoveTrack(@Path("id") int id, @Body MoveRequest body);

  @GET("/playlists/{id}/tracks")
  Single<PaginatedResponse<PlaylistTrack>> getPlaylistTracks(@Path("id") Long id,
                                                             @Query("offset") int offset,
                                                             @Query("limit") int limit);

  @DELETE("/playlists/{id}/tracks")
  Single<BaseResponse> deletePlaylistTrack(@Path("id") int id, @Query("index") int index);

  @DELETE("/playlists/{id}")
  Single<BaseResponse> deletePlaylist(@Path("id") int id);

  @PUT("/playlists")
  Single<BaseResponse> createPlaylist(@Body PlaylistRequest body);

  @GET("/playlists")
  Observable<PaginatedResponse<PlaylistDto>> getPlaylists(@Query("offset") int offset,
      @Query("limit") int limit);



  @PUT("/playlists/{id}/tracks")
  Single<BaseResponse> addTracksToPlaylist(@Path("id") int id, @Body PlaylistRequest body);

}
