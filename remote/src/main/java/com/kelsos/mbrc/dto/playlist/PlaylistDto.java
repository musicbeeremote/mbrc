package com.kelsos.mbrc.dto.playlist;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "name",
    "tracks",
    "read_only",
    "path",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class PlaylistDto {

  @JsonProperty("name") private String name;
  @JsonProperty("tracks") private Integer tracks;
  @JsonProperty("read_only") private Boolean readOnly;
  @JsonProperty("path") private String path;
  @JsonProperty("id") private Integer id;
  @JsonProperty("date_added") private String dateAdded;
  @JsonProperty("date_updated") private String dateUpdated;
  @JsonProperty("date_deleted") private String dateDeleted;

  /**
   * @return The name
   */
  @JsonProperty("name") public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  @JsonProperty("name") public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The tracks
   */
  @JsonProperty("tracks") public Integer getTracks() {
    return tracks;
  }

  /**
   * @param tracks The tracks
   */
  @JsonProperty("tracks") public void setTracks(Integer tracks) {
    this.tracks = tracks;
  }

  /**
   * @return The readOnly
   */
  @JsonProperty("read_only") public Boolean getReadOnly() {
    return readOnly;
  }

  /**
   * @param readOnly The read_only
   */
  @JsonProperty("read_only") public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * @return The path
   */
  @JsonProperty("path") public String getPath() {
    return path;
  }

  /**
   * @param path The path
   */
  @JsonProperty("path") public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return The id
   */
  @JsonProperty("id") public Integer getId() {
    return id;
  }

  /**
   * @param id The id
   */
  @JsonProperty("id") public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return The dateAdded
   */
  @JsonProperty("date_added") public String getDateAdded() {
    return dateAdded;
  }

  /**
   * @param dateAdded The date_added
   */
  @JsonProperty("date_added") public void setDateAdded(String dateAdded) {
    this.dateAdded = dateAdded;
  }

  /**
   * @return The dateUpdated
   */
  @JsonProperty("date_updated") public String getDateUpdated() {
    return dateUpdated;
  }

  /**
   * @param dateUpdated The date_updated
   */
  @JsonProperty("date_updated") public void setDateUpdated(String dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  /**
   * @return The dateDeleted
   */
  @JsonProperty("date_deleted") public String getDateDeleted() {
    return dateDeleted;
  }

  /**
   * @param dateDeleted The date_deleted
   */
  @JsonProperty("date_deleted") public void setDateDeleted(String dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(name)
        .append(tracks)
        .append(readOnly)
        .append(path)
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
    if (!(other instanceof PlaylistDto)) {
      return false;
    }
    PlaylistDto rhs = ((PlaylistDto) other);
    return new EqualsBuilder().append(name, rhs.name)
        .append(tracks, rhs.tracks)
        .append(readOnly, rhs.readOnly)
        .append(path, rhs.path)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }
}
