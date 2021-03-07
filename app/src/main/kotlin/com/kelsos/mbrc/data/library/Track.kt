package com.kelsos.mbrc.data.library

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

@JsonPropertyOrder("artist", "title", "src", "trackno", "disc")
@Table(name = "track", database = RemoteDatabase::class)
data class Track(
  @JsonProperty("artist")
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
  @Column(name="date_added")
  var dateAdded: Long = 0,
  @JsonIgnore
  @Column
  @PrimaryKey(autoincrement = true)
  var id: Long = 0
) : Model {
  /**
   * Loads from the database the most recent version of the model based on it's primary keys.
   */
  override fun load() = modelAdapter<Track>().load(this)

  override fun insert(): Long = modelAdapter<Track>().insert(this)

  override fun save(): Boolean = modelAdapter<Track>().save(this)

  override fun update(): Boolean = modelAdapter<Track>().update(this)

  override fun exists(): Boolean = modelAdapter<Track>().exists(this)

  override fun delete(): Boolean = modelAdapter<Track>().delete(this)
}
