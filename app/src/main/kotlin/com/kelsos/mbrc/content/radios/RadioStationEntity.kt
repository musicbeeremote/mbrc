package com.kelsos.mbrc.content.radios

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
  tableName = "radio_station",
  indices = [(Index("url", name = "radio_url_idx", unique = true))]
)
data class RadioStationEntity(
  @ColumnInfo(name = "name")
  override var name: String = "",
  @ColumnInfo(name = "url")
  override var url: String = "",
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : RadioStation