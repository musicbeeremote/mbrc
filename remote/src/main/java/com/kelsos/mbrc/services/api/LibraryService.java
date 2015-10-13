package com.kelsos.mbrc.services.api;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Artist;
import com.kelsos.mbrc.dto.Cover;
import com.kelsos.mbrc.dto.Genre;
import com.kelsos.mbrc.dto.LibraryAlbum;
import com.kelsos.mbrc.dto.Track;
import com.kelsos.mbrc.rest.responses.PaginatedResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Single;

public interface LibraryService {

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

}
