package com.kelsos.mbrc.content.library.albums

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface AlbumDao {
  @Query("DELETE from album")
  fun deleteAll()

  @Insert
  fun insert(list: List<AlbumEntity>)

  @Query("select * from album")
  fun getAll(): LiveData<List<AlbumEntity>>

  @Query("select * from album where album.album like '%' || :term || '%'")
  fun search(term: String): LiveData<List<AlbumEntity>>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("delete from album where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on track.album = album.album and track.album_artist = album.artist " +
      "where track.artist = :artist or track.album_artist = :artist " +
      "order by album.artist asc, album.album asc")
  fun getAlbumsByArtist(artist: String): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.album asc")
  fun getSortedByAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.album desc")
  fun getSortedByAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist asc, album.album asc")
  fun getSortedByAlbumArtistAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist desc, album.album desc")
  fun getSortedByAlbumArtistAndAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist asc, track.year asc, album.album asc")
  fun getSortedByAlbumArtistAndYearAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist desc, track.year desc, album.album desc")
  fun getSortedByAlbumArtistAndYearAndAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.artist asc, album.album asc")
  fun getSortedByArtistAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.artist desc, album.album desc")
  fun getSortedByArtistAndAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.genre asc, album.artist asc, album.album asc")
  fun getSortedByGenreAndAlbumArtistAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.genre desc, album.artist desc, album.album desc")
  fun getSortedByGenreAndAlbumArtistAndAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year asc, album.album asc")
  fun getSortedByYearAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year desc, album.album desc")
  fun getSortedByYearAndAlbumDesc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year asc, album.artist asc, album.album asc")
  fun getSortedByYearAndAlbumArtistAndAlbumAsc(): LiveData<List<AlbumEntity>>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year desc, album.artist desc, album.album desc")
  fun getSortedByYearAndAlbumArtistAndAlbumDesc(): LiveData<List<AlbumEntity>>
}
