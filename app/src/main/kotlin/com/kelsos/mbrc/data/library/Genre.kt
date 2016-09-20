package com.kelsos.mbrc.data.library

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.data.db.CacheDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("genre", "count")
@Table(name = "genre", database = CacheDatabase::class)
data class Genre(@JsonProperty("genre")
                 @Column
                 var genre: String? = null,
                 @JsonProperty("count")
                 @Column
                 var count: Int = 0,
                 @JsonIgnore
                 @Column
                 @PrimaryKey(autoincrement = true)
                 var id: Long = 0) : Model {
  override fun insert() {
    modelAdapter<Genre>().insert(this)
  }

  override fun save() {
    modelAdapter<Genre>().save(this)
  }

  override fun update() {
    modelAdapter<Genre>().update(this)
  }

  override fun exists(): Boolean {
    return modelAdapter<Genre>().exists(this)
  }

  override fun delete() {
    modelAdapter<Genre>().delete(this)
  }
}

