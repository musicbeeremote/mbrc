package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("title", "position", "disc", "genre_id", "artist_id", "album_artist_id", "album_id", "year", "path", "id", "date_added", "date_updated", "date_deleted")
class TrackDto {

    /**
     * @return The title
     */
    /**
     * @param title The title
     */
    @JsonProperty("title") var title: String? = null
    /**
     * @return The position
     */
    /**
     * @param position The position
     */
    @JsonProperty("position") var position: Int = 0
    @JsonProperty("disc") var disc: Int = 0
    /**
     * @return The genreId
     */
    /**
     * @param genreId The genre_id
     */
    @JsonProperty("genre_id") var genreId: Int = 0
    /**
     * @return The artistId
     */
    /**
     * @param artistId The artist_id
     */
    @JsonProperty("artist_id") var artistId: Int = 0
    /**
     * @return The albumArtistId
     */
    /**
     * @param albumArtistId The album_artist_id
     */
    @JsonProperty("album_artist_id") var albumArtistId: Int = 0
    /**
     * @return The albumId
     */
    /**
     * @param albumId The album_id
     */
    @JsonProperty("album_id") var albumId: Int = 0
    /**
     * @return The year
     */
    /**
     * @param year The year
     */
    @JsonProperty("year") var year: String? = null
    /**
     * @return The path
     */
    /**
     * @param path The path
     */
    @JsonProperty("path") var path: String? = null
    /**
     * @return The id
     */
    /**
     * @param id The id
     */
    @JsonProperty("id") var id: Int = 0
    /**
     * @return The dateAdded
     */
    /**
     * @param dateAdded The date_added
     */
    @JsonProperty("date_added") var dateAdded: Long = 0
    /**
     * @return The dateUpdated
     */
    /**
     * @param dateUpdated The date_updated
     */
    @JsonProperty("date_updated") var dateUpdated: Long = 0
    /**
     * @return The dateDeleted
     */
    /**
     * @param dateDeleted The date_deleted
     */
    @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
