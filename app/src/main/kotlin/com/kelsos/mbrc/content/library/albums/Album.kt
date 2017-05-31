package com.kelsos.mbrc.content.library.albums

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.interfaces.data.Data
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "album", database = RemoteDatabase::class)
data class Album(
    @JsonProperty("artist")
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
    var id: Long = 0
) : Data
