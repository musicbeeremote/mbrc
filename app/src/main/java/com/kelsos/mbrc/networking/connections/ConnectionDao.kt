package com.kelsos.mbrc.networking.connections

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ConnectionDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(settings: ConnectionSettingsEntity): Long

  @Delete
  fun delete(settings: ConnectionSettingsEntity)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(settings: ConnectionSettingsEntity)

  @Query("select * from settings")
  fun getAll(): PagingSource<Int, ConnectionSettingsEntity>

  @Query("select * from settings")
  fun all(): List<ConnectionSettingsEntity>

  @Query("select count(*) from settings")
  fun count(): Long

  @Query("select * from settings limit 1")
  fun first(): ConnectionSettingsEntity?

  @Query("select * from settings order by id desc limit 1")
  fun last(): ConnectionSettingsEntity?

  @Query("select * from settings where id < :id order by id desc limit 1")
  fun getPrevious(id: Long): ConnectionSettingsEntity?

  @Query("select id from settings where address = :address and port = :port")
  fun findId(address: String, port: Int): Long?

  @Query("select * from settings where is_default = 1")
  fun getDefault(): ConnectionSettingsEntity?

  @Query("update settings set is_default = 1 where id = :id")
  fun setDefault(id: Long)

  @Query("update settings set is_default = NULL")
  fun clearDefaults()

  @Transaction
  fun updateDefault(id: Long) {
    clearDefaults()
    setDefault(id)
  }
}
