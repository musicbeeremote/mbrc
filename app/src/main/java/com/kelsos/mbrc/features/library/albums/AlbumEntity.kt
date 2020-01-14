package com.kelsos.mbrc.features.library.albums

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "album",
  indices = [Index("artist", "album", name = "album_info_idx", unique = true)]
)
data class AlbumEntity(
  @ColumnInfo
  var artist: String,
  @ColumnInfo
  var album: String,
  @ColumnInfo
  var cover: String? = null,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
)
