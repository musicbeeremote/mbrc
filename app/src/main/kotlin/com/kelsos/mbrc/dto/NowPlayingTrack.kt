package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("artist", "title", "position", "path", "id", "date_added")
class NowPlayingTrack {

  @JsonProperty("id") var id: Int = 0

  @JsonProperty("artist") var artist: String = String.empty

  @JsonProperty("title") var title: String = String.empty

  @JsonProperty("position") var position: Int = 0

  @JsonProperty("path") var path: String = String.empty

  @JsonProperty("date_added") var dateAdded: Long = 0

  @JsonProperty("date_updated") var dateUpdated: Long = 0

  @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
