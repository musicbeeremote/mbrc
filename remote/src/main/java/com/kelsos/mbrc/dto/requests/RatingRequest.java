package com.kelsos.mbrc.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "rating"
}) public class RatingRequest {

  @JsonProperty("rating") private float rating;

  /**
   * @return The rating
   */
  @JsonProperty("rating") public float getRating() {
    return rating;
  }

  /**
   * @param rating The rating
   */
  @JsonProperty("rating") public RatingRequest setRating(float rating) {
    this.rating = rating;
    return this;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(rating).toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof RatingRequest)) {
      return false;
    }
    RatingRequest rhs = ((RatingRequest) other);
    return new EqualsBuilder().append(rating, rhs.rating)
        .isEquals();
  }
}
