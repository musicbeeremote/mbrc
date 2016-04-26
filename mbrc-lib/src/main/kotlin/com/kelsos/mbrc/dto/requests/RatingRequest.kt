package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("rating")
class RatingRequest {

    @JsonProperty("rating") private var rating: Float = 0.toFloat()

    /**
     * @return The rating
     */
    @JsonProperty("rating") fun getRating(): Float {
        return rating
    }

    /**
     * @param rating The rating
     */
    @JsonProperty("rating") fun setRating(rating: Float): RatingRequest {
        this.rating = rating
        return this
    }

}
