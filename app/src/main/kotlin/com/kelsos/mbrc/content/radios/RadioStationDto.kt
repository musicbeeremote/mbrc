package com.kelsos.mbrc.content.radios

import com.fasterxml.jackson.annotation.JsonProperty

data class RadioStationDto(
  @JsonProperty("name")
  var name: String = "",
  @JsonProperty("url")
  var url: String = ""
)
