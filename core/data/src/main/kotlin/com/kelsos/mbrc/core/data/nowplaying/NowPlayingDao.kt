package com.kelsos.mbrc.core.data.nowplaying

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

@Dao
interface NowPlayingDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<NowPlayingEntity>)

  @Query("delete from now_playing")
  fun deleteAll()

  @Query("select * from now_playing order by position")
  fun getAll(): PagingSource<Int, NowPlayingEntity>

  @Query("select * from now_playing order by position")
  fun all(): List<NowPlayingEntity>

  @Query("select id, position, path from now_playing where position in (:positions)")
  fun cachedRange(positions: List<Int>): List<CachedNowPlaying>

  /**
   * Reconciles a single page of now-playing entries against the database within one transaction.
   *
   * Only the rows whose [NowPlayingEntity.position] appears in this page are read back (bounded to
   * the page size, never the whole queue), so memory stays flat regardless of how large the remote
   * now-playing list is. Existing rows keep their primary key id so UI identity is preserved; the
   * rest are inserted as new rows.
   */
  @Transaction
  fun reconcilePage(entities: List<NowPlayingEntity>) {
    if (entities.isEmpty()) {
      return
    }
    val cached = cachedRange(
      entities.map {
        it.position
      }
    ).associateBy { "${it.path}-${it.position}" }
    val update = mutableListOf<NowPlayingEntity>()
    val new = mutableListOf<NowPlayingEntity>()
    for (entity in entities) {
      val match = cached["${entity.path}-${entity.position}"]
      if (match != null) {
        update.add(entity.copy(id = match.id))
      } else {
        new.add(entity)
      }
    }
    Timber.v("updating ${update.size} and inserting ${new.size} items")
    update(update)
    insertAll(new)
  }

  @Query(
    """
      select * from now_playing
      where title like '%' || :term || '%'
      or artist like '%' || :term || '%'
      """
  )
  fun search(term: String): PagingSource<Int, NowPlayingEntity>

  @Query(
    """
      select * from now_playing
      where title like '%' || :term || '%'
      or artist like '%' || :term || '%'
      """
  )
  fun simpleSearch(term: String): List<NowPlayingEntity>

  @Query("select count(*) from now_playing")
  fun count(): Long

  @Query("select count(*) from now_playing")
  fun observeCount(): Flow<Int>

  @Query("delete from now_playing where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("delete from now_playing where position = :position")
  fun removeByPosition(position: Int): Int

  @Query("update now_playing set position = position - 1 where position > :position ")
  fun updateRemoved(position: Int): Int

  @Transaction
  fun remove(position: Int) {
    val deleted = removeByPosition(position)
    val updated = updateRemoved(position)
    Timber.v("deleted $deleted rows and updated $updated")
  }

  @Query("select id from now_playing where position = :position")
  fun findIdByPosition(position: Int): Long

  @Query("update now_playing set position = :position where id = :id")
  fun updatePosition(id: Long, position: Int)

  @Query(
    """
    update now_playing set position = position - 1
    where position > :from
    and position <= :to
    """
  )
  fun updateMovedDown(from: Int, to: Int): Int

  @Query(
    """
    update now_playing set position = position + 1
    where position < :from
    and position >= :to
    """
  )
  fun updateMovedUp(from: Int, to: Int): Int

  @Transaction
  fun move(from: Int, to: Int) {
    val fromId = findIdByPosition(from)
    if (from < to) {
      updateMovedDown(from, to)
    } else if (from > to) {
      updateMovedUp(from, to)
    }

    updatePosition(fromId, to)
  }

  @Query(
    """
        select position from now_playing
        where title like '%' || :query || '%'
        or artist like '%' || :query || '%'
        """
  )
  fun findPositionByQuery(query: String): Int?

  @Query(
    """
        select position, title from now_playing
        where title like '%' || :query || '%'
        or artist like '%' || :query || '%'
        limit 1
        """
  )
  fun searchTrack(query: String): SearchResult?

  @Update
  fun update(existing: List<NowPlayingEntity>)

  @Query("select * from now_playing where id = :id")
  fun getById(id: Long): NowPlayingEntity?
}
