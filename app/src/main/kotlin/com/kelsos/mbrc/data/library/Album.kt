package com.kelsos.mbrc.data.library

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.data.db.CacheDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(name = "album", database = CacheDatabase::class)
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
  override fun insert() {
    modelAdapter<Album>().insert(this)
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
