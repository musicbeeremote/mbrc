package com.kelsos.mbrc.features.library.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GenreDao {

  @Query("delete from genre")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.ABORT)
  fun insertAll(list: List<GenreEntity>)

  @Update
  fun update(list: List<GenreEntity>)

  @Query("select * from genre order by genre")
  fun getAll(): PagingSource<Int, GenreEntity>

  @Query("select * from genre order by genre")
  fun all(): List<GenreEntity>

  @Query("select id, genre from genre order by genre")
  fun genres(): List<Genre>

  @Query("select * from genre where genre like '%' || :term || '%' order by genre")
  fun search(term: String): PagingSource<Int, GenreEntity>

  @Query("select count(*) from genre")
  fun count(): Long

  @Query("delete from genre where date_added < :added")
  fun removePreviousEntries(added: Long)

  @Query("select * from genre where id = :id")
  fun getById(id: Long): GenreEntity?
}
