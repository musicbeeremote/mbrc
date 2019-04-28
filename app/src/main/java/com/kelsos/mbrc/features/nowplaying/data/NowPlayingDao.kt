package com.kelsos.mbrc.features.nowplaying.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NowPlayingDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<NowPlayingEntity>)

  @Query("delete from now_playing")
  fun deleteAll()

  @Query("select * from now_playing")
  fun getAll(): DataSource.Factory<Int, NowPlayingEntity>

  @Query(
    "select * from now_playing where title " +
      "like '%' || :term || '%' or artist like '%' || :term || '%'"
  )
  fun search(term: String): DataSource.Factory<Int, NowPlayingEntity>

  @Query("select count(*) from now_playing")
  fun count(): Long

  @Query("delete from now_playing where date_added != :added")
  fun removePreviousEntries(added: Long)
}