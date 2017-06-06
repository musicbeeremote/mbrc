package com.kelsos.mbrc.content.library

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import javax.inject.Inject

class LibraryApiImpl
@Inject
constructor(
    repository: ConnectionRepository,
    private val mapper: ObjectMapper,
    clientInformationStore: ClientInformationStore
) : ApiBase(repository, mapper, clientInformationStore), LibraryService {

  override fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseGenres, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<Genre>>(it.data as String) }
    }
  }

  override fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseArtists, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<Artist>>(it.data as String) }
    }
  }

  override fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseAlbums, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<Album>>(it.data as String) }
    }
  }

  override fun getTracks(offset: Int, limit: Int): Observable<Page<Track>> {
    val range = getPageRange(offset, limit)
    return request(Protocol.LibraryBrowseTracks, range).flatMap {
      Observable.fromCallable { mapper.readValue<Page<Track>>(it.data as String) }
    }
  }
}
