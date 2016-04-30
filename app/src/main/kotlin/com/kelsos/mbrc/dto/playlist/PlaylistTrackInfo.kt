package com.kelsos.mbrc.dto.playlist

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("path", "artist", "title", "id", "date_added", "date_updated", "date_deleted")
class PlaylistTrackInfo {


    @JsonProperty("path") var path: String = String.empty

    @JsonProperty("artist") var artist: String = String.empty

    @JsonProperty("title") var title: String = String.empty

    @JsonProperty("id") var id: Long = 0

    @JsonProperty("date_added") var dateAdded: Long = 0

    @JsonProperty("date_updated") var dateUpdated: Long = 0

    @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
