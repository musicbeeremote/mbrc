package com.kelsos.mbrc.content.nowplaying

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingTrack(
    @JsonProperty("artist")
    val artist: String,
    @JsonProperty("album")
    val album: String,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("year")
    val year: String,
    @JsonProperty("path")
    val path: String
)
