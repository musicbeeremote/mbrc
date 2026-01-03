package com.kelsos.mbrc.core.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "settings",
  indices = [Index(value = ["address", "port"], unique = true)]
)
data class ConnectionSettingsEntity(
  @ColumnInfo(name = "address")
  val address: String,
  @ColumnInfo(name = "port")
  val port: Int,
  @ColumnInfo(name = "name")
  val name: String,
  @ColumnInfo(name = "is_default")
  val isDefault: Boolean? = null,
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0
)
