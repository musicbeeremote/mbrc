package com.kelsos.mbrc.content.library.tracks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "track", indices = [Index("src", name = "track_src_index", unique = true)])
data class TrackEntity(
  @ColumnInfo
  var artist: String = "",
  @ColumnInfo
  var title: String = "",
  @ColumnInfo
  var src: String = "",
  @ColumnInfo
  var trackno: Int = 0,
  @ColumnInfo
  var disc: Int = 0,
  @ColumnInfo(name = "album_artist")
  var albumArtist: String = "",
  @ColumnInfo
  var album: String = "",
  @ColumnInfo
  var genre: String = "",
  @ColumnInfo
  var year: String = "",
  @ColumnInfo(name = "sortable_year")
  var sortableYear: String = "",
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
)
