package com.kelsos.mbrc.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("name", "url")
class Playlist {

  @JsonProperty var name: String? = null
  @JsonProperty var url: String? = null
}
