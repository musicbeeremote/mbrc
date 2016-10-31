package com.kelsos.mbrc.data.library

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(name = "album", database = RemoteDatabase::class)
data class Album(@JsonProperty("artist")
                 @Column
                 var artist: String? = null,
                 @JsonProperty("album")
                 @Column
                 var album: String? = null,
                 @JsonProperty("count")
                 @Column
                 var count: Int = 0,
                 @JsonIgnore
                 @Column
                 @PrimaryKey(autoincrement = true)
                 var id: Long = 0) : Model {
  /**
   * Loads from the database the most recent version of the model based on it's primary keys.
   */
  override fun load() {
    modelAdapter<Album>().load(this)
  }

  override fun insert(): Long {
    return modelAdapter<Album>().insert(this)
  }

  override fun save() {
    modelAdapter<Album>().save(this)
  }

  override fun update() {
    modelAdapter<Album>().update(this)
  }

  override fun exists(): Boolean {
    return modelAdapter<Album>().exists(this)
  }

  override fun delete() {
    modelAdapter<Album>().delete(this)
  }
}
