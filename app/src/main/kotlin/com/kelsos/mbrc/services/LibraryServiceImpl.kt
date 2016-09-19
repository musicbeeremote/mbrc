package com.kelsos.mbrc.services

import com.fasterxml.jackson.core.type.TypeReference
import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import rx.Observable
import java.io.IOException
import javax.inject.Inject

class LibraryServiceImpl
@Inject
constructor() : ServiceBase(), LibraryService {

  override fun getGenres(offset: Int, limit: Int): Observable<Page<Genre>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseGenres, range ?: "").flatMap<Page<Genre>>({ socketMessage ->
      Observable.create<Page<Genre>> { subscriber ->
        try {
          val typeReference = object : TypeReference<Page<Genre>>() {

          }
          val page = mapper!!.readValue<Page<Genre>>(socketMessage.getData() as String, typeReference)
          subscriber.onNext(page)
          subscriber.onCompleted()
        } catch (e: IOException) {
          subscriber.onError(e)
        }
      }
    })
  }

  override fun getArtists(offset: Int, limit: Int): Observable<Page<Artist>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseArtists, range ?: "").flatMap<Page<Artist>>({ socketMessage ->
      Observable.create<Page<Artist>> { subscriber ->
        try {
          val typeReference = object : TypeReference<Page<Artist>>() {

          }
          val page = mapper!!.readValue<Page<Artist>>(socketMessage.getData() as String, typeReference)
          subscriber.onNext(page)
          subscriber.onCompleted()
        } catch (e: IOException) {
          subscriber.onError(e)
        }
      }
    })
  }

  override fun getAlbums(offset: Int, limit: Int): Observable<Page<Album>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseAlbums, range ?: "").flatMap<Page<Album>>({ socketMessage ->
      Observable.create<Page<Album>> { subscriber ->
        try {
          val typeReference = object : TypeReference<Page<Album>>() {

          }
          val page = mapper!!.readValue<Page<Album>>(socketMessage.getData() as String, typeReference)
          subscriber.onNext(page)
          subscriber.onCompleted()
        } catch (e: IOException) {
          subscriber.onError(e)
        }
      }
    })
  }

  override fun getTracks(offset: Int, limit: Int): Observable<Page<Track>> {
    val range = getPageRange(offset, limit)

    return request(Protocol.LibraryBrowseTracks, range ?: "").flatMap<Page<Track>>({ socketMessage ->
      Observable.create<Page<Track>> { subscriber ->
        try {
          val typeReference = object : TypeReference<Page<Track>>() {

          }
          val page = mapper!!.readValue<Page<Track>>(socketMessage.getData() as String, typeReference)
          subscriber.onNext(page)
          subscriber.onCompleted()
        } catch (e: IOException) {
          subscriber.onError(e)
        }
      }
    })
  }

}
