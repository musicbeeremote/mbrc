package com.kelsos.mbrc.content.nowplaying

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingTrack(
    @JsonProperty("artist") var artist: String,
    @JsonProperty("album") var album: String,
    @JsonProperty("title") var title: String,
    @JsonProperty("year") var year: String,
    @JsonProperty("path") var path: String
)
