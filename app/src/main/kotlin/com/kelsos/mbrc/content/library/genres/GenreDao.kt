package com.kelsos.mbrc.content.library.genres

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GenreDao {

  @Query("delete from genre")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<GenreEntity>)

  @Query("select * from genre order by genre")
  fun getAll(): DataSource.Factory<Int, GenreEntity>

  @Query("select * from genre where genre like '%' || :term || '%' order by genre")
  fun search(term: String): DataSource.Factory<Int, GenreEntity>

  @Query("select count(*) from genre")
  fun count(): Long

  @Query("delete from genre where date_added < :added")
  fun removePreviousEntries(added: Long)
}
