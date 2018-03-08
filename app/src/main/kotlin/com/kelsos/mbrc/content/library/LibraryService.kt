package com.kelsos.mbrc.content.library

import com.kelsos.mbrc.content.library.albums.AlbumDto
import com.kelsos.mbrc.content.library.artists.ArtistDto
import com.kelsos.mbrc.content.library.genres.GenreDto
import com.kelsos.mbrc.content.library.tracks.TrackDto
import com.kelsos.mbrc.networking.protocol.Page
import io.reactivex.Observable

interface LibraryService {
  fun getGenres(offset: Int, limit: Int): Observable<Page<GenreDto>>

  fun getArtists(offset: Int, limit: Int): Observable<Page<ArtistDto>>

  fun getAlbums(offset: Int, limit: Int): Observable<Page<AlbumDto>>

  fun getTracks(offset: Int, limit: Int): Observable<Page<TrackDto>>
}