package com.kelsos.mbrc.services.api;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.library.ArtistDto;
import com.kelsos.mbrc.dto.library.Cover;
import com.kelsos.mbrc.dto.library.GenreDto;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import com.kelsos.mbrc.dto.PaginatedResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Streaming;
import rx.Observable;

public interface LibraryService {

  @GET("/library/genres")
  Observable<PaginatedResponse<GenreDto>> getLibraryGenres(@Query("offset") int offset,
                                                    @Query("limit") int limit);

  @GET("/library/tracks")
  Observable<PaginatedResponse<TrackDto>> getLibraryTracks(@Query("offset") int offset,
                                                    @Query("limit") int limit);

  @GET("/library/covers")
  Observable<PaginatedResponse<Cover>> getLibraryCovers(@Query("offset") int offset,
                                                    @Query("limit") int limit);

  @GET("/library/artists")
  Observable<PaginatedResponse<ArtistDto>> getLibraryArtists(@Query("offset") int offset,
                                                      @Query("limit") int limit);

  @GET("/library/albums")
  Observable<PaginatedResponse<AlbumDto>> getLibraryAlbums(@Query("offset") int offset,
                                                           @Query("limit") int limit);

  @Streaming
  @GET("/library/covers/{id}/raw")
  Observable<Bitmap> getCoverById(@Path("id") long id);

}
