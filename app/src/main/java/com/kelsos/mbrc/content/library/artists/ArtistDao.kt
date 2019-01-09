package com.kelsos.mbrc.content.library.artists

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtistDao {

  @Query("delete from artist")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(list: List<ArtistEntity>)

  @Query(
    """
      select distinct artist.id, artist.artist, artist.date_added
      from artist inner join track on artist.artist = track.artist
      where track.genre = :genre group by artist.artist order by artist.artist asc
      """
  )
  fun getArtistByGenre(genre: String): PagingSource<Int, ArtistEntity>

  @Query("select * from artist where artist like '%' || :term || '%' ")
  fun search(term: String): PagingSource<Int, ArtistEntity>

  @Query("select count(*) from artist")
  fun count(): Long

  @Query("delete from artist where date_added < :added")
  fun removePreviousEntries(added: Long)

  @Query("select * from artist order by artist collate nocase asc")
  fun getAll(): PagingSource<Int, ArtistEntity>

  @Query(
    """
      select distinct artist.id, artist.artist, artist.date_added
      from artist inner join track on artist.artist = track.artist
      where track.album_artist = artist.artist group by artist.artist order by artist.artist asc
    """
  )
  fun getAlbumArtists(): PagingSource<Int, ArtistEntity>
}
