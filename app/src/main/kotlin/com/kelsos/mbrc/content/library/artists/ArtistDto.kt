package com.kelsos.mbrc.content.library.artists

import com.fasterxml.jackson.annotation.JsonProperty

data class ArtistDto(
    @JsonProperty("artist")
    var artist: String = ""
)
