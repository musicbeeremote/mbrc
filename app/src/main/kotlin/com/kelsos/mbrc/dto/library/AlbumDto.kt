package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("name", "artist_id", "cover_id", "album_id", "id", "date_added", "date_updated", "date_deleted")
class AlbumDto {

    @JsonProperty("name") var name: String = String.empty

    @JsonProperty("artist_id") var artistId: Long = 0

    @JsonProperty("cover_id") var coverId: Long = 0

    @JsonProperty("album_id") var albumId: String = String.empty

    @JsonProperty("id") var id: Long = 0

    @JsonProperty("date_added") var dateAdded: Long = 0

    @JsonProperty("date_updated") var dateUpdated: Long = 0

    @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
