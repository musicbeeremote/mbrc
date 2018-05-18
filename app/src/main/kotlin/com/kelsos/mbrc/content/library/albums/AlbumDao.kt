package com.kelsos.mbrc.content.library.albums

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface AlbumDao {
  @Query("DELETE from album")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(list: List<AlbumEntity>)

  @Query("select * from album")
  fun getAll(): DataSource.Factory<Int, AlbumEntity>

  @Query("select * from album where album.album like '%' || :term || '%'")
  fun search(term: String): DataSource.Factory<Int, AlbumEntity>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("delete from album where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query(
    "select distinct album.album, album.artist, album.id, album.date_added from album " +
      "inner join track on track.album = album.album and track.album_artist = album.artist " +
      "where track.artist = :artist or track.album_artist = :artist " +
      "order by album.artist asc, album.album asc"
  )
  fun getAlbumsByArtist(artist: String): DataSource.Factory<Int, AlbumEntity>

  @Query(BY_ALBUM_ASC)
  fun getSortedByAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album, 1,1) from ($BY_ALBUM_ASC)")
  fun getSortedByAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_ALBUM_DESC)
  fun getSortedByAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album, 1,1) from ($BY_ALBUM_DESC)")
  fun getSortedByAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_ALBUMARTIST_ALBUM_ASC)
  fun getSortedByAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album_artist, 1,1) from ($BY_ALBUMARTIST_ALBUM_ASC)")
  fun getSortedByAlbumArtistAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_ALBUMARTIST_ALBUM_DESC)
  fun getSortedByAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album_artist, 1,1) from ($BY_ALBUMARTIST_ALBUM_DESC)")
  fun getSortedByAlbumArtistAndAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_ALBUMARTIST_YEAR_ALBUM_ASC)
  fun getSortedByAlbumArtistAndYearAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album_artist, 1, 1) from ($BY_ALBUMARTIST_YEAR_ALBUM_ASC)")
  fun getSortedByAlbumArtistAndYearAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_ALBUMARTIST_YEAR_ALBUM_DESC)
  fun getSortedByAlbumArtistAndYearAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(album_artist, 1, 1) from ($BY_ALBUMARTIST_YEAR_ALBUM_DESC)")
  fun getSortedByAlbumArtistAndYearAndAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_ARTIST_AND_ALBUM_ASC)
  fun getSortedByArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(artist, 1,1) from ($BY_ARTIST_AND_ALBUM_ASC)")
  fun getSortedByArtistAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_ARTIST_AND_ALBUM_DESC)
  fun getSortedByArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(artist, 1,1) from ($BY_ARTIST_AND_ALBUM_DESC)")
  fun getSortedByArtistAndAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_GENRE_ALBUMARTIST_ALBUM_ASC)
  fun getSortedByGenreAndAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(genre, 1,1) from ($BY_GENRE_ALBUMARTIST_ALBUM_ASC)")
  fun getSortedByGenreAndAlbumArtistAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_GENRE_ALBUMARTIST_ALBUM_DESC)
  fun getSortedByGenreAndAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(genre, 1,1) from ($BY_GENRE_ALBUMARTIST_ALBUM_DESC)")
  fun getSortedByGenreAndAlbumArtistAndAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_YEAR_ALBUM_ASC)
  fun getSortedByYearAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(sortable_year, 1,4) from ($BY_YEAR_ALBUM_ASC)")
  fun getSortedByYearAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_YEAR_ALBUM_DESC)
  fun getSortedByYearAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(sortable_year, 1,4) from ($BY_YEAR_ALBUM_DESC)")
  fun getSortedByYearAndAlbumDescIndexes(): LiveData<List<String>>

  @Query(BY_YEAR_ALBUMARTIST_ALBUM_ASC)
  fun getSortedByYearAndAlbumArtistAndAlbumAsc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(sortable_year, 1,4) from ($BY_YEAR_ALBUMARTIST_ALBUM_ASC)")
  fun getSortedByYearAndAlbumArtistAndAlbumAscIndexes(): LiveData<List<String>>

  @Query(BY_YEAR_ALBUMARTIST_ALBUM_DESC)
  fun getSortedByYearAndAlbumArtistAndAlbumDesc(): DataSource.Factory<Int, AlbumEntity>

  @Query("select substr(sortable_year, 1,4) from ($BY_YEAR_ALBUMARTIST_ALBUM_DESC)")
  fun getSortedByYearAndAlbumArtistAndAlbumDescIndexes(): LiveData<List<String>>

  companion object {
    private const val BASE =
      "select distinct album.album, album.artist, track.album_artist, track.sortable_year," +
        " track.genre, album.id, album.date_added " +
        "from album inner join track on album.album = track.album " +
        "and album.artist = track.album_artist "

    const val BY_ALBUM_ASC = "$BASE order by album.album asc"
    const val BY_ALBUM_DESC = "$BASE order by album.album desc"
    const val BY_ALBUMARTIST_ALBUM_ASC = "$BASE order by album.artist asc, album.album asc"
    const val BY_ALBUMARTIST_ALBUM_DESC = "$BASE order by album.artist desc, album.album desc"
    const val BY_ALBUMARTIST_YEAR_ALBUM_ASC =
      "$BASE order by album.artist asc, track.year asc, album.album asc"
    const val BY_ALBUMARTIST_YEAR_ALBUM_DESC =
      "$BASE order by album.artist desc, track.year desc, album.album desc"
    const val BY_ARTIST_AND_ALBUM_ASC = "$BASE order by track.artist asc, album.album asc"
    const val BY_ARTIST_AND_ALBUM_DESC = "$BASE order by track.artist desc, album.album desc"
    const val BY_GENRE_ALBUMARTIST_ALBUM_ASC =
      "$BASE order by track.genre asc, album.artist asc, album.album asc"
    const val BY_GENRE_ALBUMARTIST_ALBUM_DESC =
      "$BASE order by track.genre desc, album.artist desc, album.album desc"
    const val BY_YEAR_ALBUM_ASC = "$BASE order by track.sortable_year asc, album.album asc"
    const val BY_YEAR_ALBUM_DESC = "$BASE order by track.sortable_year desc, album.album desc"
    const val BY_YEAR_ALBUMARTIST_ALBUM_ASC =
      "$BASE order by track.sortable_year asc, album.artist asc, album.album asc"
    const val BY_YEAR_ALBUMARTIST_ALBUM_DESC =
      "$BASE order by track.sortable_year desc, album.artist desc, album.album desc"
  }
}