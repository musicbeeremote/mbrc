package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import rx.Observable

interface LibraryService {
  fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>>

  fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>>

  fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>>

  fun getTracks(offset: Int, limit: Int): Observable<Page<Track>>
}
