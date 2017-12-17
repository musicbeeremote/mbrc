package com.kelsos.mbrc.content.library.genres

import com.fasterxml.jackson.annotation.JsonProperty

data class GenreDto(
    @JsonProperty("genre")
    var genre: String = ""
)
