package com.kelsos.mbrc.content.playlists

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface PlaylistDao {
  @Query("delete from playlists")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<PlaylistEntity>)

  @Query("select * from playlists")
  fun getAll(): LiveData<List<PlaylistEntity>>

  @Query("select * from playlists where name like '%'|| :term ||'%'")
  fun search(term: String): LiveData<List<PlaylistEntity>>

  @Query("select count(*) from playlists")
  fun count(): Long

  @Query("delete from playlists where dated_added != :added")
  fun removePreviousEntries(added: Long)
}
