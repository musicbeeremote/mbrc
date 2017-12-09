package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.library.tracks.Track_Table.src
import com.kelsos.mbrc.content.library.tracks.Track_Table.title
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LocalTrackDataSource
@Inject constructor() : LocalDataSource<Track> {
  override fun deleteAll() {
    delete(Track::class).execute()
  }

  override fun saveAll(list: List<Track>) {
    val adapter = modelAdapter<Track>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<List<Track>> {
    return Observable.create {
      val modelQueriable = (select from Track::class)
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  fun getAlbumTracks(album: String, artist: String): Single<List<Track>> {
    return Single.create<List<Track>> {
      val modelQueriable = (select from Track::class
          where Track_Table.album.`is`(album)
          and Track_Table.album_artist.`is`(artist))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  fun getNonAlbumTracks(artist: String): Single<List<Track>> {
    return Single.create<List<Track>> {
      val modelQueriable = (select from Track::class
          where Track_Table.album.`is`("")
          and Track_Table.artist.`is`(artist))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun search(term: String): Single<List<Track>> {
    return Single.create<List<Track>> {
      val modelQueriable = (select from Track::class where title.like("%${term.escapeLike()}%"))
      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  fun getGenreTrackPaths(genre: String): Single<List<String>> {
    return Single.fromCallable {
      val trackList = (select from Track::class
          where Track_Table.genre.`is`(genre))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
          .queryList().filter { !it.src.isNullOrEmpty() }.map { it.src!! }

      return@fromCallable trackList
    }
  }

  fun getArtistTrackPaths(artist: String): Single<List<String>> {
    return Single.fromCallable {
      val trackList =   SQLite.select().from(Track::class).where(Track_Table.artist.`is`(artist)).or(Track_Table.album_artist.`is`(artist))
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
          .queryList().filter { !it.src.isNullOrEmpty() }.map { it.src!! }

      return@fromCallable trackList
    }
  }

  fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    return Single.fromCallable {
      val trackList = (select from Track::class
          where Track_Table.album_artist.`is`(artist)
          and Track_Table.album.`is`(album))
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
          .queryList().filter { !it.src.isNullOrEmpty() }.map { it.src!! }

      return@fromCallable trackList
    }
  }

  fun getAllTrackPaths(): Single<List<String>> {
    return Single.fromCallable {
      val trackList = (select from Track::class)
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
          .queryList().filter { !it.src.isNullOrEmpty() }.map { it.src!! }

      return@fromCallable trackList
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Track::class.java).longValue() == 0L
    }
  }

  fun deletePaths(paths: List<String>) {
    val deleted = SQLite.delete()
        .from(Track::class.java)
        .where(src.notIn(paths))
        .executeUpdateDelete()
    Timber.v("$deleted entries deleted from tracks")
  }
}
