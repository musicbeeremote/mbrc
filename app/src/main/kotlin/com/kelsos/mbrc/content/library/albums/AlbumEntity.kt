package com.kelsos.mbrc.content.library.albums

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
  override var artist: String,
  @ColumnInfo
  override var album: String,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Album