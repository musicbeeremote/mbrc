package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.SocketMessage
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import io.reactivex.Observable
import java.io.IOException
import javax.inject.Inject

class LibraryServiceImpl
@Inject
constructor() : ServiceBase(), LibraryService {

  override fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseGenres, range ?: "").flatMap {
      message: SocketMessage? ->
      return@flatMap Observable.create<Page<Genre>> {
        try {
          val typeReference = object : TypeReference<Page<Genre>>() {}
          val page = mapper.readValue<Page<Genre>>(message!!.data as String, typeReference)
          it.onNext(page)
          it.onComplete()
        } catch (e: IOException) {
          it.onError(e)
        }
      }
    }
  }

  override fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseArtists, range ?: "").flatMap {
      socketMessage ->
      return@flatMap Observable.create<Page<Artist>> {
        try {
          val typeReference = object : TypeReference<Page<Artist>>() {}
          val page = mapper.readValue<Page<Artist>>(socketMessage.data as String, typeReference)
          it.onNext(page)
          it.onComplete()
        } catch (e: IOException) {
          it.onError(e)
        }
      }
    }
  }

  override fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseAlbums, range ?: "").flatMap{
      socketMessage ->
      return@flatMap Observable.create<Page<Album>> {
        try {
          val typeReference = object : TypeReference<Page<Album>>() { }
          val page = mapper.readValue<Page<Album>>(socketMessage.data as String, typeReference)
          it.onNext(page)
          it.onComplete()
        } catch (e: IOException) {
          it.onError(e)
        }
      }
    }
  }

  override fun getTracks(offset: Int, limit: Int): Observable<Page<Track>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseTracks, range ?: "").flatMap {
      socketMessage ->
      return@flatMap Observable.create<Page<Track>> {
        try {
          val typeReference = object : TypeReference<Page<Track>>() { }
          val page = mapper.readValue<Page<Track>>(socketMessage.data as String, typeReference)
          it.onNext(page)
          it.onComplete()
        } catch (e: IOException) {
          it.onError(e)
        }
      }
    }
  }

}
