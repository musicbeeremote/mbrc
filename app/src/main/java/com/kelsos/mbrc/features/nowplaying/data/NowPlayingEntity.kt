package com.kelsos.mbrc.features.nowplaying.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "now_playing",
  indices = [Index("path", "position", name = "now_playing_track_idx", unique = true)],
)
data class NowPlayingEntity(
  @ColumnInfo(name = "title")
  var title: String = "",
  @ColumnInfo(name = "artist")
  var artist: String = "",
  @ColumnInfo(name = "path")
  var path: String = "",
  @ColumnInfo(name = "position")
  var position: Int = 0,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0,
)
