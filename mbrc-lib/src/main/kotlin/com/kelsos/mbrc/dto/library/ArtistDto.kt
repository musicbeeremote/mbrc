package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.empty

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("name", "genre", "image_url", "id", "date_added", "date_updated", "date_deleted")
class ArtistDto {

    /**
     * @return The name
     */
    /**
     * @param name The name
     */
    @JsonProperty("name") var name: String = String.empty
    /**
     * @return The genre
     */
    /**
     * @param genre The genre
     */
    @JsonProperty("genre") var genre: Int = 0
    /**
     * @return The imageUrl
     */
    /**
     * @param imageUrl The image_url
     */
    @JsonProperty("image_url") var imageUrl: String = String.empty
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
