package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.kelsos.mbrc.data.library.Track_Table.title
import com.kelsos.mbrc.extensions.escapeLike
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
import rx.Single
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

    database<Track>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Track>> {
    return Observable.fromEmitter({
      val modelQueriable = (select from Track::class)
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)

  }

  fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {
      val modelQueriable = (select from Track::class
          where Track_Table.album.`is`(album)
          and Track_Table.album_artist.`is`(artist))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {
      val modelQueriable = (select from Track::class
          where Track_Table.album.`is`("")
          and Track_Table.artist.`is`(artist))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun search(term: String): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {
      val modelQueriable = (select from Track::class where title.like("%${term.escapeLike()}%"))
      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
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
      return@fromCallable SQLite.selectCountOf().from(Track::class.java).count() == 0L
    }
  }
}
