package com.kelsos.mbrc.dto.playlist

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("name", "tracks", "read_only", "path", "id", "date_added", "date_updated", "date_deleted")
class PlaylistDto {

    /**
     * @return The name
     */
    /**
     * @param name The name
     */
    @JsonProperty("name") var name: String = String.empty
    /**
     * @return The tracks
     */
    /**
     * @param tracks The tracks
     */
    @JsonProperty("tracks") var tracks: Int = 0
    /**
     * @return The readOnly
     */
    @JsonProperty("read_only") var readOnly: Boolean = false
        private set
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
