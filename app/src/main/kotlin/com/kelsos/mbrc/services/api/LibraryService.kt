package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.constants.Constants.LIMIT
import com.kelsos.mbrc.dto.PageResponse
import com.kelsos.mbrc.dto.library.AlbumDto
import com.kelsos.mbrc.dto.library.ArtistDto
import com.kelsos.mbrc.dto.library.CoverDto
import com.kelsos.mbrc.dto.library.GenreDto
import com.kelsos.mbrc.dto.library.TrackDto
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface LibraryService {

  @GET("/library/genres")
  fun getLibraryGenres(@Query("after") after: Long = 0,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = LIMIT): Observable<PageResponse<GenreDto>>

  @GET("/library/tracks")
  fun getLibraryTracks(@Query("after") after: Long = 0,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = LIMIT): Observable<PageResponse<TrackDto>>

  @GET("/library/covers")
  fun getLibraryCovers(@Query("after") after: Long = 0,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = LIMIT): Observable<PageResponse<CoverDto>>

  @GET("/library/artists")
  fun getLibraryArtists(@Query("after") after: Long = 0,
                        @Query("offset") offset: Int = 0,
                        @Query("limit") limit: Int = LIMIT): Observable<PageResponse<ArtistDto>>

  @GET("/library/albums")
  fun getLibraryAlbums(@Query("after") after: Long = 0,
                       @Query("offset") offset: Int = 0,
                       @Query("limit") limit: Int = LIMIT): Observable<PageResponse<AlbumDto>>
}
