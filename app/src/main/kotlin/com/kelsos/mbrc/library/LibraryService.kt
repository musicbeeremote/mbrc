package com.kelsos.mbrc.library

import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.library.albums.Album
import com.kelsos.mbrc.library.artists.Artist
import com.kelsos.mbrc.library.genres.Genre
import com.kelsos.mbrc.library.tracks.Track
import io.reactivex.Observable

interface LibraryService {
  fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>>

  fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>>

  fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>>

  fun getTracks(offset: Int, limit: Int): Observable<Page<Track>>
}
