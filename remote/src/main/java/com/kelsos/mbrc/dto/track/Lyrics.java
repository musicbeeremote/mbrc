package com.kelsos.mbrc.dto.track;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.dto.BaseResponse;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "lyrics"
})
public class Lyrics extends BaseResponse {

  @JsonProperty("lyrics") private String lyrics;

  /**
   * @return The lyrics
   */
  @JsonProperty("lyrics") public String getLyrics() {
    return lyrics;
  }

  /**
   * @param lyrics The lyrics
   */
  @JsonProperty("lyrics") public void setLyrics(String lyrics) {
    this.lyrics = lyrics;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(lyrics)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof Lyrics)) {
      return false;
    }
    Lyrics rhs = ((Lyrics) other);
    return new EqualsBuilder().append(lyrics, rhs.lyrics)
        .isEquals();
  }
}
