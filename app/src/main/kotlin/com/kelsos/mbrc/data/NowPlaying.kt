package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
@JsonIgnoreProperties(ignoreUnknown = true)

@JsonPropertyOrder("artist", "title", "path", "position")
@Table(database = RemoteDatabase::class, name = "now_playing")
data class NowPlaying(
  @JsonProperty("title")
  @Column(name = "title")
  var title: String? = null,
  @JsonProperty("artist")
  @Column(name = "artist")
  var artist: String? = null,
  @JsonProperty("path")
  @Column(name = "path")
  var path: String? = null,
  @JsonProperty("position")
  @Column(name = "position")
  var position: Int = 0,
  @JsonIgnore
  @PrimaryKey(autoincrement = true)
  var id: Long = 0
) : Model {
  /**
   * Loads from the database the most recent version of the model based on it's primary keys.
   */
  override fun load() = modelAdapter<NowPlaying>().load(this)

  override fun insert(): Long = modelAdapter<NowPlaying>().insert(this)

  override fun save(): Boolean = modelAdapter<NowPlaying>().save(this)

  override fun update(): Boolean = modelAdapter<NowPlaying>().update(this)

  override fun exists(): Boolean = modelAdapter<NowPlaying>().exists(this)

  override fun delete(): Boolean = modelAdapter<NowPlaying>().delete(this)
}
