package com.kelsos.mbrc.features.library.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "artist",
  indices = [(Index("artist", name = "artist_artist_idx", unique = true))]
)
data class ArtistEntity(
  @ColumnInfo
  var artist: String,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
)