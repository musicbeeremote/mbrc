package com.kelsos.mbrc.content.radios

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface RadioStationDao {

  @Query("delete from radio_station")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<RadioStationEntity>)

  @Query("select * from radio_station")
  fun getAll(): DataSource.Factory<Int, RadioStationEntity>

  @Query("select * from radio_station where name like '%' || :term || '%' ")
  fun search(term: String): DataSource.Factory<Int, RadioStationEntity>

  @Query("select count(*) from radio_station")
  fun count(): Long

  @Query("delete from radio_station where date_added != :added")
  fun removePreviousEntries(added: Long)
}