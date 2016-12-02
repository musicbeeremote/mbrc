package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class, name = "covers")

data class Cover(
    @Column var hash: String = "",
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<Cover>().load(this)

  override fun insert(): Long = modelAdapter<Cover>().insert(this)

  override fun save() = modelAdapter<Cover>().save(this)

  override fun update() = modelAdapter<Cover>().update(this)

  override fun exists(): Boolean = modelAdapter<Cover>().exists(this)

  override fun delete() = modelAdapter<Cover>().delete(this)
}
