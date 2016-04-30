package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("title", "position", "disc", "genre_id", "artist_id", "album_artist_id", "album_id", "year", "path", "id", "date_added", "date_updated", "date_deleted")
class TrackDto {

    @JsonProperty("title") var title: String = String.empty

    @JsonProperty("position") var position: Int = 0

    @JsonProperty("disc") var disc: Int = 0

    @JsonProperty("genre_id") var genreId: Long = 0

    @JsonProperty("artist_id") var artistId: Long = 0

    @JsonProperty("album_artist_id") var albumArtistId: Long = 0

    @JsonProperty("album_id") var albumId: Long = 0

    @JsonProperty("year") var year: String = String.empty

    @JsonProperty("path") var path: String = String.empty

    @JsonProperty("id") var id: Long = 0

    @JsonProperty("date_added") var dateAdded: Long = 0

    @JsonProperty("date_updated") var dateUpdated: Long = 0

    @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
