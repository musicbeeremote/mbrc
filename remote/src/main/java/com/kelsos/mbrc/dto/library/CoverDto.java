package com.kelsos.mbrc.dto.library;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "hash",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class CoverDto {

  @JsonProperty("hash") private String hash;
  @JsonProperty("id") private int id;
  @JsonProperty("date_added") private long dateAdded;
  @JsonProperty("date_updated") private long dateUpdated;
  @JsonProperty("date_deleted") private long dateDeleted;

  /**
   * @return The hash
   */
  @JsonProperty("hash") public String getHash() {
    return hash;
  }

  /**
   * @param hash The hash
   */
  @JsonProperty("hash") public void setHash(String hash) {
    this.hash = hash;
  }

  /**
   * @return The id
   */
  @JsonProperty("id") public int getId() {
    return id;
  }

  /**
   * @param id The id
   */
  @JsonProperty("id") public void setId(int id) {
    this.id = id;
  }

  /**
   * @return The dateAdded
   */
  @JsonProperty("date_added") public long getDateAdded() {
    return dateAdded;
  }

  /**
   * @param dateAdded The date_added
   */
  @JsonProperty("date_added") public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  /**
   * @return The dateUpdated
   */
  @JsonProperty("date_updated") public long getDateUpdated() {
    return dateUpdated;
  }

  /**
   * @param dateUpdated The date_updated
   */
  @JsonProperty("date_updated") public void setDateUpdated(long dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  /**
   * @return The dateDeleted
   */
  @JsonProperty("date_deleted") public long getDateDeleted() {
    return dateDeleted;
  }

  /**
   * @param dateDeleted The date_deleted
   */
  @JsonProperty("date_deleted") public void setDateDeleted(long dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(hash)
        .append(id)
        .append(dateAdded)
        .append(dateUpdated)
        .append(dateDeleted)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof CoverDto)) {
      return false;
    }
    CoverDto rhs = ((CoverDto) other);
    return new EqualsBuilder().append(hash, rhs.hash)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }
}
