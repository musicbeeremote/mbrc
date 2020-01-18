package com.kelsos.mbrc.features.library.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "genre",
  indices = [(Index("genre", name = "genre_genre_idx", unique = true))]
)
data class GenreEntity(
  @ColumnInfo
  var genre: String = "",
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @ColumnInfo(name = "date_updated")
  var dateUpdated: Long = 0,
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
)