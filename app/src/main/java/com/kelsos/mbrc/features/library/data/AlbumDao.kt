package com.kelsos.mbrc.features.library.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlbumDao {
  @Query("DELETE from album")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(list: List<AlbumEntity>)

  @Query("select * from album order by album collate nocase asc")
  fun getAll(): DataSource.Factory<Int, AlbumEntity>

  @Query("select * from album where album like '%' || :term || '%'")
  fun search(term: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("delete from album where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query(
    """
        select distinct album.artist as artist, album.album as album,
        album.date_added as date_added, album.id as id from album
        inner join track where album.album = track.album and track.album_artist = album.artist
        and (track.artist = :artist or track.album_artist = :artist) order by artist asc, album asc
        """
  )
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select * from album where id = :id")
  fun getById(id: Long): AlbumEntity?
}
