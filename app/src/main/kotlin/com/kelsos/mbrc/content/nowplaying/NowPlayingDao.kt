package com.kelsos.mbrc.content.nowplaying

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface NowPlayingDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<NowPlayingEntity>)

  @Query("delete from now_playing")
  fun deleteAll()

  @Query("select * from now_playing")
  fun getAll(): LiveData<List<NowPlayingEntity>>

  @Query("select * from now_playing where title like '%' || :term || '%' or artist like '%' || :term || '%'")
  fun search(term: String): LiveData<List<NowPlayingEntity>>

  @Query("select count(*) from now_playing")
  fun count(): Long

  @Query("delete from now_playing where date_added != :added")
  fun removePreviousEntries(added: Long)

}
