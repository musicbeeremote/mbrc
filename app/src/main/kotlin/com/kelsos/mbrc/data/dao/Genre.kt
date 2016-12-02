package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.Index
import com.raizlabs.android.dbflow.annotation.IndexGroup
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class,
    name = "genres",
    indexGroups = arrayOf(IndexGroup(number = 1, name = "genre_name_index")))
data class Genre(
    @Column @Index(indexGroups = intArrayOf(1)) var name: String = "",
    @Column(name = "date_added") var dateAdded: Long = 0,
    @Column(name = "date_updated") var dateUpdated: Long = 0,
    @Column(name = "date_deleted") var dateDeleted: Long = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<Genre>().load(this)

  override fun insert(): Long = modelAdapter<Genre>().insert(this)

  override fun save() = modelAdapter<Genre>().save(this)

  override fun update() = modelAdapter<Genre>().update(this)

  override fun exists(): Boolean = modelAdapter<Genre>().exists(this)

  override fun delete() = modelAdapter<Genre>().delete(this)
}
