package com.kelsos.mbrc.networking.connections

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "settings",
  indices = [(Index(value = ["address", "port"], unique = true))]
)
data class ConnectionSettingsEntity(
  @ColumnInfo(name = "address")
  var address: String = "",
  @ColumnInfo(name = "port")
  var port: Int = 0,
  @ColumnInfo(name = "name")
  var name: String = "",
  @PrimaryKey(autoGenerate = true)
  var id: Long = 0
)
