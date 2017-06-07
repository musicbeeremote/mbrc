package com.kelsos.mbrc.content.now_playing

import com.fasterxml.jackson.annotation.JsonProperty

data class NowPlayingTrack(
    @JsonProperty("artist") var artist: String,
    @JsonProperty("album") var album: String,
    @JsonProperty("title") var title: String,
    @JsonProperty("year") var year: String,
    @JsonProperty("path") var path: String
)
