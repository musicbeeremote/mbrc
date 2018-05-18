package com.kelsos.mbrc.content.library.tracks

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

@Dao
interface TrackDao {
  @Query("delete from track")
  fun deleteAll()

  @Insert(onConflict = OnConflictStrategy.FAIL)
  fun insertAll(list: List<TrackEntity>)

  @Query("select * from track order by album_artist asc, album asc, disc asc, trackno asc")
  fun getAll(): DataSource.Factory<Int, TrackEntity>

  @Query("select substr(album_artist,1,1) from track order by album_artist asc, album asc," +
    " disc asc, trackno asc")
  fun getAllIndexes(): LiveData<List<String>>

  @Query("select * from track where '%' || :term ||'%' order by album_artist asc, " +
    "album asc, disc asc, trackno asc")
  fun search(term: String): DataSource.Factory<Int, TrackEntity>

  @Query("select * from track where album = :album and album_artist = :artist " +
    "order by album_artist asc, album asc, disc asc, trackno asc")
  fun getAlbumTracks(
    album: String,
    artist: String
  ): DataSource.Factory<Int, TrackEntity>

  @Query("select * from track where album = '' and album_artist = :artist " +
    "order by album_artist asc, album asc, disc asc, trackno asc")
  fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, TrackEntity>

  @Query("select src from track where genre = :genre " +
    "order by album_artist asc, album asc, disc asc, trackno asc")
  fun getGenreTrackPaths(genre: String): List<String>

  @Query("select src from track where artist = :artist or album_artist = :artist " +
    "order by album_artist asc, album asc, disc asc, trackno asc")
  fun getArtistTrackPaths(artist: String): List<String>

  @Query("select src from track where album_artist = :artist or album = :album " +
    "order by album_artist asc, album asc, disc asc, trackno asc")
  fun getAlbumTrackPaths(album: String, artist: String): List<String>

  @Query("select src from track order by album_artist asc, album asc, disc asc, trackno asc")
  fun getAllTrackPaths(): List<String>

  @Query("select count(*) from track")
  fun count(): Long

  @Query("delete from track where src in (:paths)")
  fun deletePaths(paths: List<String>)

  @Query("delete from track where date_added != :added")
  fun removePreviousEntries(added: Long)

  @Query("select src, id from track where src in (:paths)")
  fun findMatchingIds(paths: List<String>): List<TrackPath>

  @Update
  fun update(data: List<TrackEntity>)
}