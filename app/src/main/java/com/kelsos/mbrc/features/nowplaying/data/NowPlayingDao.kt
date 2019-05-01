package com.kelsos.mbrc.features.nowplaying.data

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import timber.log.Timber

@Dao
interface NowPlayingDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<NowPlayingEntity>)

  @Query("delete from now_playing")
  fun deleteAll()

  @Query("select * from now_playing order by position")
  fun getAll(): DataSource.Factory<Int, NowPlayingEntity>

  @Query("select * from now_playing order by position")
  fun all(): List<NowPlayingEntity>

  @Query(
    """
      select * from now_playing
      where title like '%' || :term || '%'
      or artist like '%' || :term || '%'
      """
  )
  fun search(term: String): DataSource.Factory<Int, NowPlayingEntity>

  @Query("select count(*) from now_playing")
  fun count(): Long

  @Query("delete from now_playing where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("delete from now_playing where position = :position")
  fun removeByPosition(position: Int): Int

  @Query("update now_playing set position = position - 1 where position > :position ")
  fun updatePositions(position: Int): Int

  @Transaction
  fun remove(position: Int) {
    val deleted = removeByPosition(position)
    val updated = updatePositions(position)
    Timber.v("deleted $deleted rows and updated $updated")
  }

  @Query("select id from now_playing where position = :position")
  fun findIdByPosition(position: Int): Long

  @Query("update now_playing set position = :position where id = :id")
  fun updatePosition(id: Long, position: Int)

  @Transaction
  fun move(from: Int, to: Int) {
    val fromId = findIdByPosition(from)
    val toId = findIdByPosition(to)
    updatePosition(fromId, to)
    updatePosition(toId, from)
  }

  @Query(
    """
        select position from now_playing
        where title like '%' || :query || '%'
        or artist like '%' || :query || '%'
        """
  )
  fun findPositionByQuery(query: String): Int?
}