package com.kelsos.mbrc.dto.track

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

@JsonPropertyOrder("artist", "title", "album", "year", "path")
class TrackInfoResponse : BaseResponse() {

  @JsonProperty("artist")
  var artist: String = ""

  @JsonProperty("title")
  var title: String = ""

  @JsonProperty("album")
  var album: String = ""

  @JsonProperty("year")
  var year: String = ""

  @JsonProperty("path")
  var path: String = ""
}
