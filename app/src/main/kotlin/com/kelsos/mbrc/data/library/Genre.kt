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

@JsonPropertyOrder("genre", "count")
@Table(name = "genre", database = RemoteDatabase::class)
data class Genre(
  @JsonProperty("genre")
  @Column
  var genre: String? = null,
  @JsonProperty("count")
  @Column
  var count: Int = 0,
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
  override fun load() = modelAdapter<Genre>().load(this)

  override fun insert(): Long = modelAdapter<Genre>().insert(this)

  override fun save(): Boolean = modelAdapter<Genre>().save(this)

  override fun update(): Boolean = modelAdapter<Genre>().update(this)

  override fun exists(): Boolean = modelAdapter<Genre>().exists(this)

  override fun delete(): Boolean = modelAdapter<Genre>().delete(this)
}

