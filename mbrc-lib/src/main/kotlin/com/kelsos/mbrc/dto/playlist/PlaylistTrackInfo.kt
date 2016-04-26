package com.kelsos.mbrc.dto.playlist

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("path", "artist", "title", "id", "date_added", "date_updated", "date_deleted")
class PlaylistTrackInfo {

    /**
     * @return The path
     */
    /**
     * @param path The path
     */
    @JsonProperty("path") var path: String? = null
    /**
     * @return The artist
     */
    /**
     * @param artist The artist
     */
    @JsonProperty("artist") var artist: String? = null
    /**
     * @return The title
     */
    /**
     * @param title The title
     */
    @JsonProperty("title") var title: String? = null
    /**
     * @return The id
     */
    /**
     * @param id The id
     */
    @JsonProperty("id") var id: Long = 0
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
