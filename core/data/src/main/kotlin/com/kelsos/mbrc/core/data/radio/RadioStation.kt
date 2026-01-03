package com.kelsos.mbrc.core.data.radio

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
data class RadioStation(val name: String, val url: String, val id: Long)

@Entity(
  tableName = "radio_station",
  indices = [Index("url", name = "radio_url_idx", unique = true)]
)
data class RadioStationEntity(
  @ColumnInfo(name = "name")
  val name: String,
  @ColumnInfo(name = "url")
  val url: String,
  @ColumnInfo(name = "date_added")
  val dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0
)
