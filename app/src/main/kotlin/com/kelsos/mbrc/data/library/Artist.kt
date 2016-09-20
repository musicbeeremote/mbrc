package com.kelsos.mbrc.data.library

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.data.db.CacheDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@Table(name = "artist", database = CacheDatabase::class)
data class Artist(@JsonProperty("artist")
                  @Column
                  var artist: String? = null,
                  @JsonProperty("count")
                  @Column
                  var count: Int = 0,
                  @JsonIgnore
                  @Column
                  @PrimaryKey(autoincrement = true)
                  var id: Long = 0) : Model {
  override fun insert() {
    modelAdapter<Artist>().insert(this)
  }

  override fun save() {
    modelAdapter<Artist>().save(this)
  }

  override fun update() {
    modelAdapter<Artist>().update(this)
  }

  override fun exists(): Boolean {
    return modelAdapter<Artist>().exists(this)
  }

  override fun delete() {
    modelAdapter<Artist>().delete(this)
  }
}
