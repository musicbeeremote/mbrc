package com.kelsos.mbrc.rest;

import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.dto.Artist;
import com.kelsos.mbrc.dto.Cover;
import com.kelsos.mbrc.dto.Genre;
import com.kelsos.mbrc.dto.LibraryAlbum;
import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.dto.Playlist;
import com.kelsos.mbrc.dto.PlaylistTrack;
import com.kelsos.mbrc.dto.Track;
import com.kelsos.mbrc.rest.requests.ChangeStateRequest;
import com.kelsos.mbrc.rest.requests.MoveRequest;
import com.kelsos.mbrc.rest.requests.NowPlayingQueueRequest;
import com.kelsos.mbrc.rest.requests.PlayPathRequest;
import com.kelsos.mbrc.rest.requests.PlaylistRequest;
import com.kelsos.mbrc.rest.requests.PositionRequest;
import com.kelsos.mbrc.rest.requests.RatingRequest;
import com.kelsos.mbrc.rest.requests.RepeatRequest;
import com.kelsos.mbrc.rest.requests.ShuffleRequest;
import com.kelsos.mbrc.rest.requests.VolumeRequest;
import com.kelsos.mbrc.rest.responses.LyricsResponse;
import com.kelsos.mbrc.rest.responses.PaginatedResponse;
import com.kelsos.mbrc.rest.responses.PlayerStatusResponse;
import com.kelsos.mbrc.rest.responses.RatingResponse;
import com.kelsos.mbrc.rest.responses.ShuffleStateResponse;
import com.kelsos.mbrc.rest.responses.StateResponse;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.rest.responses.SuccessStateResponse;
import com.kelsos.mbrc.rest.responses.SuccessVolumeResponse;
import com.kelsos.mbrc.rest.responses.TextValueResponse;
import com.kelsos.mbrc.rest.responses.TrackInfo;
import com.kelsos.mbrc.rest.responses.TrackPositionResponse;
import com.kelsos.mbrc.rest.responses.ValueResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Observable;

public interface RemoteApi {
  @GET("/player/volume")
  Observable<ValueResponse> getVolume();

  @PUT("/player/volume")
  Observable<SuccessVolumeResponse> updateVolume(@Body VolumeRequest body);

  @PUT("/track/rating")
  Observable<SuccessResponse> updateRating(@Body RatingRequest body);

  @PUT("/track/position")
  Observable<TrackPositionResponse> updatePosition(@Body PositionRequest body);

  @PUT("/player/shuffle")
  Observable<SuccessStateResponse> updateShuffleState(@Body ShuffleRequest body);

  @PUT("/player/scrobble")
  Observable<SuccessResponse> updateScrobbleState(@Body ChangeStateRequest body);

  @PUT("/player/repeat")
  Observable<SuccessResponse> updateRepeatState(@Body RepeatRequest body);

  @PUT("/player/mute")
  Observable<SuccessResponse> updateMuteState(@Body ChangeStateRequest body);

  @PUT("/playlists/play")
  Observable<SuccessResponse> playPlaylist(@Body PlayPathRequest body);

  @DELETE("/nowplaying/{id}")
  Observable<SuccessResponse> nowPlayingRemoveTrack(@Path("id") int id);

  @PUT("/nowplaying/play")
  Observable<SuccessResponse> nowPlayingPlayTrack(@Body PlayPathRequest body);

  @PUT("/nowplaying/move")
  Observable<SuccessResponse> nowPlayingMoveTrack(@Body MoveRequest body);

  @PUT("/playlists/{id}/tracks/move")
  Observable<SuccessResponse> playlistMoveTrack(@Path("id") int id, @Body MoveRequest body);

  @GET("/track/rating")
  Observable<RatingResponse> getTrackRating();

  @GET("/track/position")
  Observable<TrackPositionResponse> getCurrentPosition();

  @GET("/track/lyrics")
  Observable<LyricsResponse> getTrackLyrics();

  @GET("/track/cover")
  @Streaming
  Observable<Response> getTrackCover(@Query("t") String timestamp);

  @GET("/track")
  Observable<TrackInfo> getTrackInfo();

  @GET("/player/shuffle")
  Observable<ShuffleStateResponse> getShuffleState();

  @GET("/player/scrobble")
  Observable<StateResponse> getScrobbleState();

  @GET("/player/repeat")
  Observable<TextValueResponse> getRepeatMode();

  @GET("/player/playstate")
  Observable<TextValueResponse> getPlaystate();

  @GET("/playlists/{id}/tracks")
  Observable<PaginatedResponse<PlaylistTrack>> getPlaylistTracks(@Path("id") Long id,
      @Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/player/status")
  Observable<PlayerStatusResponse> getPlayerStatus();

  @GET("/player/mute")
  Observable<StateResponse> getMuteState();

  @GET("/library/genres")
  Observable<PaginatedResponse<Genre>> getLibraryGenres(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/tracks")
  Observable<PaginatedResponse<Track>> getLibraryTracks(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/covers")
  Observable<PaginatedResponse<Cover>> getLibraryCovers(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/artists")
  Observable<PaginatedResponse<Artist>> getLibraryArtists(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/albums")
  Observable<PaginatedResponse<LibraryAlbum>> getLibraryAlbums(@Query("offset") int offset,
      @Query("limit") int limit);

  @Streaming
  @GET("/library/covers/{id}/raw")
  Observable<Response> getCoverById(@Path("id") long id);

  @GET("/library/autodj")
  Observable<StateResponse> getAutodjState();

  @DELETE("/playlists/{id}/tracks")
  Observable<SuccessResponse> deletePlaylistTrack(@Path("id") int id, @Query("index") int index);

  @DELETE("/playlists/{id}")
  Observable<SuccessResponse> deletePlaylist(@Path("id") int id);

  @PUT("/playlists")
  Observable<SuccessResponse> createPlaylist(@Body PlaylistRequest body);

  @GET("/playlists")
  Observable<PaginatedResponse<Playlist>> getPlaylists(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/nowplaying")
  Observable<PaginatedResponse<NowPlayingTrack>> getNowPlayingList(@Query("offset") int offset,
      @Query("limit") int limit);

  @PUT("/playlists/{id}/tracks")
  Observable<SuccessResponse> addTracksToPlaylist(@Path("id") int id, @Body PlaylistRequest body);

  @GET("/player/action")
  Observable<SuccessResponse> performPlayerAction(@Query("action") @PlaybackAction String action);

  @PUT("/nowplaying/queue/")
  Observable<SuccessResponse> nowplayingQueue(@Body NowPlayingQueueRequest body);
}
