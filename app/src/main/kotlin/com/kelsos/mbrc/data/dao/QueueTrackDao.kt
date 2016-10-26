package com.kelsos.mbrc.data.dao

import com.kelsos.mbrc.data.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(database = RemoteDatabase::class, name = "queue_tracks")
data class QueueTrackDao(
    @Column var artist: String = "",
    @Column var title: String = "",
    @Column var path: String = "",
    @Column var position: Int = 0,
    @Column @PrimaryKey(autoincrement = true) var id: Long = 0) : Model {

  override fun load() = modelAdapter<QueueTrackDao>().load(this)

  override fun insert(): Long = modelAdapter<QueueTrackDao>().insert(this)

  override fun save() = modelAdapter<QueueTrackDao>().save(this)

  override fun update() = modelAdapter<QueueTrackDao>().update(this)

  override fun exists(): Boolean = modelAdapter<QueueTrackDao>().exists(this)

  override fun delete() = modelAdapter<QueueTrackDao>().delete(this)
}
