package com.kelsos.mbrc.data.library

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.JsonNode
import com.kelsos.mbrc.data.db.CacheDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.structure.BaseModel
import com.raizlabs.android.dbflow.structure.Model

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("artist", "title", "src", "trackno", "disc")
@Table(name = "track", database = CacheDatabase::class)
data class Track(@JsonProperty("artist")
                 @Column
                 var artist: String? = null,
                 @JsonProperty("title")
                 @Column
                 var title: String? = null,
                 @JsonProperty("src")
                 @Column
                 var src: String? = null,
                 @JsonProperty("trackno")
                 @Column
                 var trackno: Int = 0,
                 @JsonProperty("disc")
                 @Column
                 var disc: Int = 0,
                 @JsonProperty("album_artist")
                 @Column(name = "album_artist")
                 var albumArtist: String? = null,
                 @JsonProperty("album")
                 @Column
                 var album: String? = null,
                 @JsonProperty("genre")
                 @Column
                 var genre: String? = null,
                 @JsonIgnore
                 @Column
                 @PrimaryKey(autoincrement = true)
                 var id: Long = 0) : Model {
  override fun insert() {
    modelAdapter<Track>().insert(this)
  }

  override fun save() {
    modelAdapter<Track>().save(this)
  }

  override fun update() {
    modelAdapter<Track>().update(this)
  }

  override fun exists(): Boolean {
    return modelAdapter<Track>().exists(this)
  }

  override fun delete() {
    modelAdapter<Track>().delete(this)
  }
}
