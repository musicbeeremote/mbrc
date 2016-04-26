package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("name", "artist_id", "cover_id", "album_id", "id", "date_added", "date_updated", "date_deleted")
class AlbumDto {

    /**
     * @return The name
     */
    /**
     * @param name The name
     */
    @JsonProperty("name") var name: String = String.empty
    /**
     * @return The artistId
     */
    /**
     * @param artistId The artist_id
     */
    @JsonProperty("artist_id") var artistId: Int = 0
    /**
     * @return The coverId
     */
    /**
     * @param coverId The cover_id
     */
    @JsonProperty("cover_id") var coverId: Int = 0
    /**
     * @return The albumId
     */
    /**
     * @param albumId The album_id
     */
    @JsonProperty("album_id") var albumId: String = String.empty
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
