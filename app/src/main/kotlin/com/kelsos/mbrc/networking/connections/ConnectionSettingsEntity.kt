package com.kelsos.mbrc.networking.connections

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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