package com.kelsos.mbrc.features.radio.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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

  @Query("select * from radio_station where id = :id")
  fun getById(id: Long): RadioStationEntity?
}