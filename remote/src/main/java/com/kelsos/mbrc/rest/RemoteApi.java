package com.kelsos.mbrc.rest;

import com.kelsos.mbrc.rest.responses.*;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Observable;

import java.util.List;


public interface RemoteApi {
	@GET("/player/volume")
    Observable<ValueResponse> getVolume();

    @PUT("/player/volume")
    Observable<SuccessVolumeResponse> updateVolume(@Query("value") int volume);

    @PUT("/track/rating")
    Observable<SuccessResponse> updateRating(@Query("rating") float rating);

    @PUT("/track/position")
    Observable<TrackPositionResponse> updatePosition(@Query("position") int position);

    @PUT("/player/shuffle")
    Observable<SuccessResponse> updateShuffleState(@Query("enabled") boolean enabled);

    @PUT("/player/scrobble")
    Observable<SuccessResponse> updateScrobbleState(@Query("enabled") boolean enabled);

    @PUT("/player/repeat")
    Observable<SuccessResponse> updateRepeatState(@Query("enabled") String mode);

    @PUT("/player/mute")
    Observable<SuccessResponse> updateMuteState(@Query("enabled") boolean enabled);

    @PUT("/player/autodj")
    Observable<SuccessResponse> updateAutoDjState(@Query("enabled") boolean enabled);

    @GET("/player/previous")
    Observable<SuccessResponse> playPrevious();

    @GET("/player/next")
    Observable<SuccessResponse> playNext();

    @PUT("/playlists/play")
    Observable<SuccessResponse> playPlaylist(@Query("path") String path);

    @GET("/player/stop")
    Observable<SuccessResponse> playbackStop();

    @GET("/player/play")
    Observable<SuccessResponse> playbackStart();

    @GET("/player/pause")
    Observable<SuccessResponse> playbackPause();

    @DELETE("/nowplaying/{id}")
    Observable<SuccessResponse> nowPlayingRemoveTrack(@Path("id") int id);

    @PUT("/nowplaying/play")
    Observable<SuccessResponse> nowPlayingPlayTrack(@Query("path") String path);

    @PUT("/nowplaying/move")
    Observable<SuccessResponse> nowPlayingMoveTrack(@Query("from") int from, @Query("to") int to);

    @PUT("/playlists/{id}/tracks/move")
    Observable<SuccessResponse> playlistMoveTrack(@Path("id") int id,
                           @Query("from") int from,
                           @Query("to") int to);

    @GET("/track/rating")
    Observable<RatingResponse> getTrackRating();

    @GET("/track/position")
    Observable<TrackPositionResponse> getCurrentPosition();

    @GET("/track/lyrics")
    Observable<LyricsResponse> getTrackLyrics();

    @GET("/track/cover")
    Observable<CoverResponse> getTrackCover();

    @GET("/track/cover/raw")
    @Streaming
    Observable<Response> getTrackCoverData(@Query("t") String timestamp);

    @GET("/track")
    Observable<TrackResponse> getTrackInfo();

    @GET("/player/shuffle")
    Observable<StateResponse> getShuffleState();

    @PUT("/player/shuffle/toggle")
    Observable<SuccessStateResponse> toggleShuffleState();

    @GET("/player/scrobble")
    Observable<StateResponse> getScrobbleState();

    @GET("/player/repeat")
    Observable<TextValueResponse> getRepeatMode();

    @PUT("/player/changerepeat")
    Observable<TextValueResponse> changeRepeatMode();

    @GET("/player/playstate")
    Observable<TextValueResponse> getPlaystate();

    @GET("/playlists/{id}/tracks")
    Observable<PaginatedDataResponse> getPlaylistTracks(@Path("id") Long id,
                           @Query("offset") int offset,
                           @Query("limit") int limit);

    @GET("/player/status")
    Observable<PlayerStatusResponse> getPlayerStatus();

    @GET("/player/mute")
    Observable<StateResponse> getMuteState();

    @GET("/library/genres")
    Observable<PaginatedDataResponse> getLibraryGenres(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/library/tracks")
    Observable<PaginatedDataResponse> getLibraryTracks(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/library/covers")
    Observable<PaginatedDataResponse> getLibraryCovers(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/library/artists")
    Observable<PaginatedDataResponse> getLibraryArtists(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/library/albums")
    Observable<PaginatedDataResponse> getLibraryAlbums(@Query("offset") int offset, @Query("limit") int limit);

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
    Observable<SuccessResponse> createPlaylist(@Query("name") String name, @Query("list") List<String> list);

    @GET("/playlists")
    Observable<PaginatedDataResponse> getPlaylists(@Query("offset") int offset, @Query("limit") int limit);

    @GET("/nowplaying")
    Observable<PaginatedDataResponse> getNowPlayingList(@Query("offset") int offset, @Query("limit") int limit);

    @PUT("/playlists/{id}/tracks")
    Observable<SuccessResponse> addTracksToPlaylist(@Path("id") int id, List<String> list);

    @PUT("/player/playpause")
    Observable<SuccessResponse> playPause();
}
