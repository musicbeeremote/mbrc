package com.kelsos.mbrc.rest;

import com.kelsos.mbrc.rest.responses.*;
import retrofit.http.*;

import java.util.List;


public interface RemoteApi {
    @GET("/player/volume")
    ValueResponse getVolume();
    @PUT("/player/volume")
    SuccessResponse updateVolume(@Query("value") int volume);
    @PUT("/track/rating")
    SuccessResponse updateRating(@Query("rating") float rating);
    @PUT("/track/position")
    TrackPositionResponse updatePosition(@Query("position") int position);
    @PUT("/player/shuffle")
    SuccessResponse updateShuffleState(@Query("enabled") boolean enabled);
    @PUT("/player/scrobble")
    SuccessResponse updateScrobbleState(@Query("enabled") boolean enabled);
    @PUT("/player/repeat")
    SuccessResponse updateRepeatState(@Query("enabled") boolean enabled);
    @PUT("/player/mute")
    SuccessResponse updateMuteState(@Query("enabled") boolean enabled);
    @PUT("/player/autodj")
    SuccessResponse updateAutoDjState(@Query("enabled") boolean enabled);
    @GET("/player/previous")
    SuccessResponse playPrevious();
    @GET("/player/next")
    SuccessResponse playNext();
    @PUT("/playlists/play")
    SuccessResponse playPlaylist(@Query("path") String path);
    @GET("/player/stop")
    SuccessResponse playbackStop();
    @GET("/player/play")
    SuccessResponse playbackStart();
    @GET("/player/pause")
    SuccessResponse playbackPause();
    @DELETE("/nowplaying/{id}")
    SuccessResponse nowPlayingRemoveTrack(@Path("id") int id);
    @PUT("/nowplaying/play")
    SuccessResponse nowPlayingPlayTrack(@Query("path") String path);
    @PUT("/nowplaying/move")
    SuccessResponse nowPlayingMoveTrack(@Query("from") int from, @Query("to") int to);
    @PUT("/playlists/{id}/tracks/move")
    SuccessResponse playlistMoveTrack(@Path("id") int id, @Query("from") int from, @Query("to") int to);
    @GET("/track/rating")
    RatingResponse getTrackRating();
    @GET("/track/position")
    TrackPositionResponse getCurrentPosition();
    @GET("/track/lyrics")
    LyricsResponse getTrackLyrics();
    @GET("/track/cover")
    CoverResponse getTrackCover();
    @GET("/track")
    TrackResponse getTrackInfo();
    @GET("/player/shuffle")
    StateResponse getShuffleState();
    @GET("/player/scrobble")
    StateResponse getScrobbleState();
    @GET("/player/repeat")
    TextValueResponse getRepeatMode();
    @GET("/player/playstate")
    TextValueResponse getPlaystate();
    @GET("/playlists/{id}/tracks")
    PaginatedDataResponse getPlaylistTracks(@Path("id") int id, @Query("offset") int offset, @Query("limit") int limit);
    @GET("/player/status")
    PlayerStatusResponse getPlayerStatus();
    @GET("/player/mute")
    StateResponse getMuteState();
    @GET("/library/genres")
    PaginatedDataResponse getLibraryGenres(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/library/tracks")
    PaginatedDataResponse getLibraryTracks(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/library/covers")
    PaginatedDataResponse getLibraryCoverss(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/library/artists")
    PaginatedDataResponse getLibraryArtists(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/library/albums")
    PaginatedDataResponse getLibraryAlbums(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/library/autodj")
    StateResponse getAutodjState();
    @DELETE("/playlists/{id}/tracks")
    SuccessResponse deletePlaylistTrack(@Path("id") int id, @Query("index") int index);
    @DELETE("/playlists/{id}")
    SuccessResponse deletePlaylist(@Path("id") int id);
    @PUT("/playlists")
    SuccessResponse createPlaylist(@Query("name") String name, @Query("list") List<String> list);
    @GET("/playlists")
    PaginatedDataResponse getPlaylists(@Query("offset") int offset, @Query("limit") int limit);
    @GET("/nowplaying")
    PaginatedDataResponse getNowPlayingList(@Query("offset") int offset, @Query("limit") int limit);
    @PUT("/playlists/{id}/tracks")
    SuccessResponse addTracksToPlaylist(@Path("id") int id, List<String> list);
}
