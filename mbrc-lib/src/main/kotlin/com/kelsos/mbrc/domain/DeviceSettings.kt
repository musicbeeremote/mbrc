package com.kelsos.mbrc.domain

import com.kelsos.mbrc.SettingsDatabase
import com.raizlabs.android.dbflow.annotation.*
import com.raizlabs.android.dbflow.structure.BaseModel

@Table(database = SettingsDatabase::class,
    name = DeviceSettings.NAME,
    uniqueColumnGroups = arrayOf(
        UniqueGroup(groupNumber = 1,
        uniqueConflict = ConflictAction.IGNORE)
    ))
class DeviceSettings : BaseModel() {
  @Column(name = "id")
  @PrimaryKey(autoincrement = true)
  var id: Long = 0

  @Column(name = "address")
  @Unique(uniqueGroups = intArrayOf(1), unique = false)
  var address: String? = null

  @Column(name = "name")
  var name: String? = null
  @Column(name = "port")
  @Unique(uniqueGroups = intArrayOf(1), unique = false)
  var port: Int = 0

  @Column(name = "http")
  @Unique(uniqueGroups = intArrayOf(1), unique = false)
  var http: Int = 0

  companion object {
    const val NAME = "settings"
  }
}
