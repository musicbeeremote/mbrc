package com.kelsos.mbrc.dto.track

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("rating")
class Rating : BaseResponse() {

    /**

     * @return
     * *     The rating
     */
    /**

     * @param rating
     * *     The rating
     */
    @JsonProperty("rating")
    var rating: Double = 0.toDouble()

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(rating).toHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is Rating) {
            return false
        }
        return EqualsBuilder().append(rating, other.rating).isEquals
    }

}
