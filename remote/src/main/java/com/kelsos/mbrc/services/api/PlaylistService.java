package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.Playlist;
import com.kelsos.mbrc.dto.PlaylistTrack;
import com.kelsos.mbrc.rest.requests.MoveRequest;
import com.kelsos.mbrc.rest.requests.PlayPathRequest;
import com.kelsos.mbrc.rest.requests.PlaylistRequest;
import com.kelsos.mbrc.rest.responses.PaginatedResponse;
import com.kelsos.mbrc.rest.responses.SuccessResponse;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Single;

public interface PlaylistService {

  @PUT("/playlists/play")
  Single<SuccessResponse> playPlaylist(@Body PlayPathRequest body);

  @PUT("/playlists/{id}/tracks/move")
  Single<SuccessResponse> playlistMoveTrack(@Path("id") int id, @Body MoveRequest body);

  @GET("/playlists/{id}/tracks")
  Single<PaginatedResponse<PlaylistTrack>> getPlaylistTracks(@Path("id") Long id,
                                                             @Query("offset") int offset,
                                                             @Query("limit") int limit);

  @DELETE("/playlists/{id}/tracks")
  Single<SuccessResponse> deletePlaylistTrack(@Path("id") int id, @Query("index") int index);

  @DELETE("/playlists/{id}")
  Single<SuccessResponse> deletePlaylist(@Path("id") int id);

  @PUT("/playlists")
  Single<SuccessResponse> createPlaylist(@Body PlaylistRequest body);

  @GET("/playlists")
  Single<PaginatedResponse<Playlist>> getPlaylists(@Query("offset") int offset,
                                                   @Query("limit") int limit);



  @PUT("/playlists/{id}/tracks")
  Single<SuccessResponse> addTracksToPlaylist(@Path("id") int id, @Body PlaylistRequest body);

}
