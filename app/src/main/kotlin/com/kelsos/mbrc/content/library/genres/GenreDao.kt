package com.kelsos.mbrc.content.library.genres

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface GenreDao {

  @Query("delete from genre")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveAll(list: List<GenreEntity>)

  @Query("select * from genre order by genre")
  fun getAll(): DataSource.Factory<Int, GenreEntity>

  @Query("select * from genre where genre like '%' || :term || '%' order by genre")
  fun search(term: String): DataSource.Factory<Int, GenreEntity>

  @Query("select count(*) from genre")
  fun count(): Long

  @Query("delete from genre where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("select substr(genre,1,1) from genre order by genre")
  fun getAllIndexes(): LiveData<List<String>>
}