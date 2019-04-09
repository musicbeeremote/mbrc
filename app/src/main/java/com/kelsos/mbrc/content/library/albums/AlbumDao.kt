package com.kelsos.mbrc.content.library.albums

import androidx.lifecycle.LiveData
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
        select * from album
        where artist = :artist order by artist asc, album asc
        """
  )
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album, 1, 1) from album order by album collate nocase asc")
  fun getIndexes(): LiveData<List<String>>
}