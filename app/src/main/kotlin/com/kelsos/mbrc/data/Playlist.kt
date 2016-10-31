package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.data.db.RemoteDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.Model

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "url")
@Table(database = RemoteDatabase::class, name = "playlists")
data class Playlist(@Column(name = "name")
                    @JsonProperty var name: String = "",
                    @Column(name = "url")
                    @JsonProperty var url: String = "",
                    @Column(name = "id")
                    @PrimaryKey(autoincrement = true)
                    @JsonIgnore
                    var id: Long = 0) : Model {
  
  override fun load() {
    modelAdapter<Playlist>().load(this)
  }

  override fun insert(): Long {
    return modelAdapter<Playlist>().insert(this)
  }

  override fun save() {
    modelAdapter<Playlist>().save(this)
  }

  override fun update() {
    modelAdapter<Playlist>().update(this)
  }

  override fun exists(): Boolean {
    return modelAdapter<Playlist>().exists(this)
  }

  override fun delete() {
    modelAdapter<Playlist>().delete(this)
  }
}
