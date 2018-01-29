package com.kelsos.mbrc.content.library.albums

import android.arch.paging.DataSource
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
  fun getAll(): DataSource.Factory<Int, AlbumEntity>

  @Query("select * from album where album.album like '%' || :term || '%'")
  fun search(term: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("delete from album where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on track.album = album.album and track.album_artist = album.artist " +
      "where track.artist = :artist or track.album_artist = :artist " +
      "order by album.artist asc, album.album asc")
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.album asc")
  fun getSortedByAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.album desc")
  fun getSortedByAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist asc, album.album asc")
  fun getSortedByAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist desc, album.album desc")
  fun getSortedByAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist asc, track.year asc, album.album asc")
  fun getSortedByAlbumArtistAndYearAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by album.artist desc, track.year desc, album.album desc")
  fun getSortedByAlbumArtistAndYearAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.artist asc, album.album asc")
  fun getSortedByArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.artist desc, album.album desc")
  fun getSortedByArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.genre asc, album.artist asc, album.album asc")
  fun getSortedByGenreAndAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.genre desc, album.artist desc, album.album desc")
  fun getSortedByGenreAndAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year asc, album.album asc")
  fun getSortedByYearAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year desc, album.album desc")
  fun getSortedByYearAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year asc, album.artist asc, album.album asc")
  fun getSortedByYearAndAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on album.album = track.album and album.artist = track.album_artist " +
      "order by track.year desc, album.artist desc, album.album desc")
  fun getSortedByYearAndAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>
}
