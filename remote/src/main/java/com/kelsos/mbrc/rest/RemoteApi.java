package com.kelsos.mbrc.rest;

import android.graphics.Bitmap;

import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.dto.Artist;
import com.kelsos.mbrc.dto.Cover;
import com.kelsos.mbrc.dto.Genre;
import com.kelsos.mbrc.dto.LibraryAlbum;
import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.dto.NowPlayingTrack;
import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Playlist;
import com.kelsos.mbrc.dto.PlaylistTrack;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.Track;
import com.kelsos.mbrc.dto.TrackInfo;
import com.kelsos.mbrc.dto.Volume;
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
import com.kelsos.mbrc.rest.responses.PaginatedResponse;
import com.kelsos.mbrc.rest.responses.PlayerStatusResponse;
import com.kelsos.mbrc.rest.responses.SuccessBooleanStateResponse;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.rest.responses.SuccessVolumeResponse;
import com.kelsos.mbrc.rest.responses.TextValueResponse;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Single;

public interface RemoteApi {
  @GET("/player/volume")
  Single<Volume> getVolume();

  @PUT("/player/volume")
  Single<SuccessVolumeResponse> updateVolume(@Body VolumeRequest body);

  @PUT("/track/rating")
  Single<SuccessResponse> updateRating(@Body RatingRequest body);

  @PUT("/track/position")
  Single<Position> updatePosition(@Body PositionRequest body);

  @PUT("/player/shuffle")
  Single<Shuffle> updateShuffleState(@Body ShuffleRequest body);

  @PUT("/player/scrobble")
  Single<SuccessResponse> updateScrobbleState(@Body ChangeStateRequest body);

  @PUT("/player/repeat")
  Single<SuccessResponse> updateRepeatState(@Body RepeatRequest body);

  @PUT("/player/mute")
  Single<SuccessResponse> updateMuteState(@Body ChangeStateRequest body);

  @PUT("/playlists/play")
  Single<SuccessResponse> playPlaylist(@Body PlayPathRequest body);

  @DELETE("/nowplaying/{id}")
  Single<SuccessResponse> nowPlayingRemoveTrack(@Path("id") int id);

  @PUT("/nowplaying/play")
  Single<SuccessResponse> nowPlayingPlayTrack(@Body PlayPathRequest body);

  @PUT("/nowplaying/move")
  Single<SuccessResponse> nowPlayingMoveTrack(@Body MoveRequest body);

  @PUT("/playlists/{id}/tracks/move")
  Single<SuccessResponse> playlistMoveTrack(@Path("id") int id, @Body MoveRequest body);

  @GET("/track/rating")
  Single<Rating> getTrackRating();

  @GET("/track/position")
  Single<Position> getCurrentPosition();

  @GET("/track/lyrics")
  Single<Lyrics> getTrackLyrics();

  @GET("/track/cover")
  @Streaming
  Single<Bitmap> getTrackCover(@Query("t") String timestamp);

  @GET("/track")
  Single<TrackInfo> getTrackInfo();

  @GET("/player/shuffle")
  Single<Shuffle> getShuffleState();

  @GET("/player/scrobble")
  Single<SuccessBooleanStateResponse> getScrobbleState();

  @GET("/player/repeat")
  Single<TextValueResponse> getRepeatMode();

  @GET("/player/playstate")
  Single<PlaybackState> getPlaystate();

  @GET("/playlists/{id}/tracks")
  Single<PaginatedResponse<PlaylistTrack>> getPlaylistTracks(@Path("id") Long id,
      @Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/player/status")
  Single<PlayerStatusResponse> getPlayerStatus();

  @GET("/player/mute")
  Single<SuccessBooleanStateResponse> getMuteState();

  @GET("/library/genres")
  Single<PaginatedResponse<Genre>> getLibraryGenres(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/tracks")
  Single<PaginatedResponse<Track>> getLibraryTracks(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/covers")
  Single<PaginatedResponse<Cover>> getLibraryCovers(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/artists")
  Single<PaginatedResponse<Artist>> getLibraryArtists(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/library/albums")
  Single<PaginatedResponse<LibraryAlbum>> getLibraryAlbums(@Query("offset") int offset,
      @Query("limit") int limit);

  @Streaming
  @GET("/library/covers/{id}/raw")
  Single<Bitmap> getCoverById(@Path("id") long id);

  @DELETE("/playlists/{id}/tracks")
  Single<SuccessResponse> deletePlaylistTrack(@Path("id") int id, @Query("index") int index);

  @DELETE("/playlists/{id}")
  Single<SuccessResponse> deletePlaylist(@Path("id") int id);

  @PUT("/playlists")
  Single<SuccessResponse> createPlaylist(@Body PlaylistRequest body);

  @GET("/playlists")
  Single<PaginatedResponse<Playlist>> getPlaylists(@Query("offset") int offset,
      @Query("limit") int limit);

  @GET("/nowplaying")
  Single<PaginatedResponse<NowPlayingTrack>> getNowPlayingList(@Query("offset") int offset,
      @Query("limit") int limit);

  @PUT("/playlists/{id}/tracks")
  Single<SuccessResponse> addTracksToPlaylist(@Path("id") int id, @Body PlaylistRequest body);

  @GET("/player/action")
  Single<SuccessResponse> performPlayerAction(@Query("action") @PlaybackAction String action);

  @PUT("/nowplaying/queue/")
  Single<SuccessResponse> nowplayingQueue(@Body NowPlayingQueueRequest body);
}
