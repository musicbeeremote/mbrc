package com.kelsos.mbrc.services.api;

import com.kelsos.mbrc.dto.PaginatedResponse;
import com.kelsos.mbrc.dto.library.AlbumDto;
import com.kelsos.mbrc.dto.library.ArtistDto;
import com.kelsos.mbrc.dto.library.CoverDto;
import com.kelsos.mbrc.dto.library.GenreDto;
import com.kelsos.mbrc.dto.library.TrackDto;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface LibraryService {

  @GET("/library/genres") Observable<PaginatedResponse<GenreDto>> getLibraryGenres(@Query("offset") int offset,
      @Query("limit") int limit,
      @Query("after") long after);

  @GET("/library/tracks") Observable<PaginatedResponse<TrackDto>> getLibraryTracks(@Query("offset") int offset,
      @Query("limit") int limit,
      @Query("after") long after);

  @GET("/library/covers") Observable<PaginatedResponse<CoverDto>> getLibraryCovers(@Query("offset") int offset,
      @Query("limit") int limit,
      @Query("after") long after);

  @GET("/library/artists") Observable<PaginatedResponse<ArtistDto>> getLibraryArtists(@Query("offset") int offset,
      @Query("limit") int limit,
      @Query("after") long after);

  @GET("/library/albums") Observable<PaginatedResponse<AlbumDto>> getLibraryAlbums(@Query("offset") int offset,
      @Query("limit") int limit,
      @Query("after") long after);
}
