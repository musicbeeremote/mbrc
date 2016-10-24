package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.SettingsDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.ConflictAction
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.annotation.Unique
import com.raizlabs.android.dbflow.annotation.UniqueGroup
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = SettingsDatabase::class,
    name = "settings",
    uniqueColumnGroups = arrayOf(UniqueGroup(groupNumber = 1,
        uniqueConflict = ConflictAction.IGNORE)))
data class DeviceSettings(
    @Column(name = "address")
    @Unique(uniqueGroups = intArrayOf(1), unique = false) var address: String,
    @Column(name = "name") var name: String,
    @Column(name = "port")
    @Unique(uniqueGroups = intArrayOf(1), unique = false) var port: Int,
    @Column(name = "id") @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<DeviceSettings>().load(this)

  override fun insert(): Long = modelAdapter<DeviceSettings>().insert(this)

  override fun save() = modelAdapter<DeviceSettings>().save(this)

  override fun update() = modelAdapter<DeviceSettings>().update(this)

  override fun exists(): Boolean = modelAdapter<DeviceSettings>().exists(this)

  override fun delete() = modelAdapter<DeviceSettings>().delete(this)
}
