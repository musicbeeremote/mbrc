package com.kelsos.mbrc.dto.playlist

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("track_info_id", "playlist_id", "position", "id", "date_added", "date_updated", "date_deleted")
class PlaylistTrack {

    /**
     * @return The trackInfoId
     */
    /**
     * @param trackInfoId The track_info_id
     */
    @JsonProperty("track_info_id") var trackInfoId: Long = 0
    /**
     * @return The playlistId
     */
    /**
     * @param playlistId The playlist_id
     */
    @JsonProperty("playlist_id") var playlistId: Long = 0
    /**
     * @return The position
     */
    /**
     * @param position The position
     */
    @JsonProperty("position") var position: Int = 0
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
