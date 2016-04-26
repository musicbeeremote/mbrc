package com.kelsos.mbrc.dto.library

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("hash", "id", "date_added", "date_updated", "date_deleted")
class CoverDto {

    /**
     * @return The hash
     */
    /**
     * @param hash The hash
     */
    @JsonProperty("hash") var hash: String? = null
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
