package com.kelsos.mbrc.content.library.artists

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.interfaces.data.Data
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table

@Table(name = "artist", database = RemoteDatabase::class)
data class Artist(
    @JsonProperty("artist")
    @Column
    var artist: String? = null,
    @JsonProperty("count")
    @Column
    var count: Int = 0,
    @JsonIgnore
    @Column
    @PrimaryKey(autoincrement = true)
    var id: Long = 0
) : Data
