package com.kelsos.mbrc.features.library.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
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
    "select distinct artist.id, artist.artist, artist.date_added " +
      "from artist inner join track on artist.artist = track.artist " +
      "where track.genre = :genre group by artist.artist order by artist.artist asc"
  )
  fun getArtistByGenre(genre: String): DataSource.Factory<Int, ArtistEntity>

  @Query("select * from artist where artist like '%' || :term || '%' ")
  fun search(term: String): DataSource.Factory<Int, ArtistEntity>

  @Query("select count(*) from artist")
  fun count(): Long

  @Query("delete from artist where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("select * from artist order by artist collate nocase asc")
  fun getAll(): DataSource.Factory<Int, ArtistEntity>

  @Query("select substr(artist,1,1)  from artist order by artist collate nocase asc")
  fun getAllIndexes(): LiveData<List<String>>

  @Query(
    "select distinct artist.id, artist.artist, artist.date_added " +
      "from artist inner join track on artist.artist = track.artist " +
      "where track.album_artist = artist.artist group by artist.artist order by artist.artist asc"
  )
  fun getAlbumArtists(): DataSource.Factory<Int, ArtistEntity>

  @Query(
    "select distinct substr(artist.artist,1,1) " +
      "from artist inner join track on artist.artist = track.artist " +
      "where track.album_artist = artist.artist group by artist.artist order by artist.artist asc"
  )
  fun getAlbumArtistIndexes(): LiveData<List<String>>

  @Query("select * from artist where id = :id")
  fun getById(id: Long): ArtistEntity?
}