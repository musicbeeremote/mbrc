package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.dto.PaginatedResponse
import com.kelsos.mbrc.dto.library.*
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

interface LibraryService {

    @GET("/library/genres") fun getLibraryGenres(@Query("offset") offset: Int,
                                                 @Query("limit") limit: Int,
                                                 @Query("after") after: Long): Observable<PaginatedResponse<GenreDto>>

    @GET("/library/tracks") fun getLibraryTracks(@Query("offset") offset: Int,
                                                 @Query("limit") limit: Int,
                                                 @Query("after") after: Long): Observable<PaginatedResponse<TrackDto>>

    @GET("/library/covers") fun getLibraryCovers(@Query("offset") offset: Int,
                                                 @Query("limit") limit: Int,
                                                 @Query("after") after: Long): Observable<PaginatedResponse<CoverDto>>

    @GET("/library/artists") fun getLibraryArtists(@Query("offset") offset: Int,
                                                   @Query("limit") limit: Int,
                                                   @Query("after") after: Long): Observable<PaginatedResponse<ArtistDto>>

    @GET("/library/albums") fun getLibraryAlbums(@Query("offset") offset: Int,
                                                 @Query("limit") limit: Int,
                                                 @Query("after") after: Long): Observable<PaginatedResponse<AlbumDto>>
}
