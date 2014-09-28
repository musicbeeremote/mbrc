package com.kelsos.mbrc.rest;

import com.kelsos.mbrc.rest.responses.*;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;


public interface RemoteApi {
    @GET("/player/volume")
    void getVolume(Callback<ValueResponse> cb);

    @PUT("/player/volume")
    void updateVolume(@Query("value") int volume, Callback<SuccessResponse> cb);

    @PUT("/track/rating")
    void updateRating(@Query("rating") float rating, Callback<SuccessResponse> cb);

    @PUT("/track/position")
    void updatePosition(@Query("position") int position, Callback<TrackPositionResponse> cb);

    @PUT("/player/shuffle")
    void updateShuffleState(@Query("enabled") boolean enabled, Callback<SuccessResponse> cb);

    @PUT("/player/scrobble")
    void updateScrobbleState(@Query("enabled") boolean enabled, Callback<SuccessResponse> cb);

    @PUT("/player/repeat")
    void updateRepeatState(@Query("enabled") boolean enabled, Callback<SuccessResponse> cb);

    @PUT("/player/mute")
    void updateMuteState(@Query("enabled") boolean enabled, Callback<SuccessResponse> cb);

    @PUT("/player/autodj")
    void updateAutoDjState(@Query("enabled") boolean enabled, Callback<SuccessResponse> cb);

    @GET("/player/previous")
    void playPrevious(Callback<SuccessResponse> cb);

    @GET("/player/next")
    void playNext(Callback<SuccessResponse> cb);

    @PUT("/playlists/play")
    void playPlaylist(@Query("path") String path, Callback<SuccessResponse> cb);

    @GET("/player/stop")
    void playbackStop(Callback<SuccessResponse> cb);

    @GET("/player/play")
    void playbackStart(Callback<SuccessResponse> cb);

    @GET("/player/pause")
    void playbackPause(Callback<SuccessResponse> cb);

    @DELETE("/nowplaying/{id}")
    void nowPlayingRemoveTrack(@Path("id") int id, Callback<SuccessResponse> cb);

    @PUT("/nowplaying/play")
    void nowPlayingPlayTrack(@Query("path") String path, Callback<SuccessResponse> cb);

    @PUT("/nowplaying/move")
    void nowPlayingMoveTrack(@Query("from") int from, @Query("to") int to, Callback<SuccessResponse> cb);

    @PUT("/playlists/{id}/tracks/move")
    void playlistMoveTrack(@Path("id") int id,
                           @Query("from") int from,
                           @Query("to") int to,
                           Callback<SuccessResponse> cb);

    @GET("/track/rating")
    void getTrackRating(Callback<RatingResponse> cb);

    @GET("/track/position")
    void getCurrentPosition(Callback<TrackPositionResponse> cb);

    @GET("/track/lyrics")
    void getTrackLyrics(Callback<LyricsResponse> cb);

    @GET("/track/cover")
    void getTrackCover(Callback<CoverResponse> cb);

    @GET("/track")
    void getTrackInfo(Callback<TrackResponse> cb);

    @GET("/player/shuffle")
    void getShuffleState(Callback<StateResponse> cb);

    @GET("/player/scrobble")
    void getScrobbleState(Callback<StateResponse> cb);

    @GET("/player/repeat")
    void getRepeatMode(Callback<TextValueResponse> cb);

    @GET("/player/playstate")
    void getPlaystate(Callback<TextValueResponse> cb);

    @GET("/playlists/{id}/tracks")
    void getPlaylistTracks(@Path("id") int id,
                           @Query("offset") int offset,
                           @Query("limit") int limit,
                           Callback<PaginatedDataResponse> cb);

    @GET("/player/status")
    void getPlayerStatus(Callback<PlayerStatusResponse> cb);

    @GET("/player/mute")
    void getMuteState(Callback<StateResponse> cb);

    @GET("/library/genres")
    void getLibraryGenres(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/library/tracks")
    void getLibraryTracks(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/library/covers")
    void getLibraryCoverss(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/library/artists")
    void getLibraryArtists(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/library/albums")
    void getLibraryAlbums(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/library/autodj")
    void getAutodjState(Callback<StateResponse> cb);

    @DELETE("/playlists/{id}/tracks")
    void deletePlaylistTrack(@Path("id") int id, @Query("index") int index, Callback<SuccessResponse> cb);

    @DELETE("/playlists/{id}")
    void deletePlaylist(@Path("id") int id, Callback<SuccessResponse> cb);

    @PUT("/playlists")
    void createPlaylist(@Query("name") String name, @Query("list") List<String> list, Callback<SuccessResponse> cb);

    @GET("/playlists")
    void getPlaylists(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @GET("/nowplaying")
    void getNowPlayingList(@Query("offset") int offset, @Query("limit") int limit, Callback<PaginatedDataResponse> cb);

    @PUT("/playlists/{id}/tracks")
    void addTracksToPlaylist(@Path("id") int id, List<String> list, Callback<SuccessResponse> cb);
}
