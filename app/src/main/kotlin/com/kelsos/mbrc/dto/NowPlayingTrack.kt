package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("artist", "title", "position", "path", "id", "date_added")
class NowPlayingTrack {

    /**
     * @return The artist
     */
    /**
     * @param artist The artist
     */
    @JsonProperty("artist") var artist: String = String.empty
    /**
     * @return The title
     */
    /**
     * @param title The title
     */
    @JsonProperty("title") var title: String = String.empty
    /**
     * @return The position
     */
    /**
     * @param position The position
     */
    @JsonProperty("position") var position: Int = 0
    /**
     * @return The path
     */
    /**
     * @param path The path
     */
    @JsonProperty("path") var path: String = String.empty
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
    @JsonProperty("date_updated") var dateUpdated: Long = 0
    @JsonProperty("date_deleted") var dateDeleted: Long = 0
}
