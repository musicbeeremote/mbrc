package com.kelsos.mbrc.content.library

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.networking.protocol.Page
import io.reactivex.Observable

interface LibraryService {
  fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>>

  fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>>

  fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>>

  fun getTracks(offset: Int, limit: Int): Observable<Page<Track>>
}
