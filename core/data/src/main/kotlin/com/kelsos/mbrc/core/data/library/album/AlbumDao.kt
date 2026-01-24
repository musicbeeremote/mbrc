package com.kelsos.mbrc.core.data.library.album

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AlbumDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(list: List<AlbumEntity>)

  @Query("select * from album order by album collate nocase asc")
  fun getAll(): PagingSource<Int, AlbumEntity>

  @Query(
    """
    select * from album
    where album like '%' || :term || '%' or artist like '%' || :term || '%'
    order by album collate nocase asc
    """
  )
  fun search(term: String): PagingSource<Int, AlbumEntity>

  @Query("select count(*) from album")
  fun count(): Long

  @Query("select * from album")
  fun all(): List<AlbumEntity>

  @Query("delete from album where date_added < :added")
  fun removePreviousEntries(added: Long)

  @Query(
    """
        SELECT album.artist AS artist, album.album AS album,
        album.date_added AS date_added, album.id AS id, album.cover AS cover
        FROM album
        INNER JOIN track ON album.album = track.album AND track.album_artist = album.artist
        WHERE track.artist = :artist OR track.album_artist = :artist
        GROUP BY album.id
        ORDER BY
          CASE WHEN MIN(track.sortable_year) = '' THEN 1 ELSE 0 END ASC,
          MIN(track.sortable_year) ASC,
          album.album COLLATE NOCASE ASC
    """
  )
  fun getAlbumsByArtist(artist: String): PagingSource<Int, AlbumEntity>

  @Query("select album, artist, cover as hash from album")
  fun getCovers(): List<AlbumCover>

  @Query("select count(*) from album where cover is not null")
  fun coverCount(): Long

  @Query("update album set cover = :cover where artist = :artist and album = :album")
  fun updateCover(artist: String, album: String, cover: String)

  @Transaction
  fun updateCovers(updated: List<AlbumCover>) {
    for ((artist, album, hash) in updated) {
      if (hash.isNullOrEmpty()) {
        continue
      }
      updateCover(
        artist = artist.orEmpty(),
        album = album.orEmpty(),
        cover = hash
      )
    }
  }

  @Query("select * from album where id = :id")
  fun getById(id: Long): AlbumEntity?

  @Query(
    """
        SELECT DISTINCT album.artist AS artist, album.album AS album,
        album.date_added AS date_added, album.id AS id, album.cover AS cover
        FROM album
        INNER JOIN track ON album.album = track.album AND track.album_artist = album.artist
        INNER JOIN genre ON genre.genre = track.genre
        WHERE genre.id = :genreId
        GROUP BY album.id
        ORDER BY album.album COLLATE NOCASE ASC
    """
  )
  fun getAlbumsByGenre(genreId: Long): PagingSource<Int, AlbumEntity>
}
