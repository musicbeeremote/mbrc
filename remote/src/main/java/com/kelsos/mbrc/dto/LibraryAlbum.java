package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "name",
    "artist_id",
    "cover_id",
    "album_id",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class LibraryAlbum {

  @JsonProperty("name") private String name;
  @JsonProperty("artist_id") private Integer artistId;
  @JsonProperty("cover_id") private Integer coverId;
  @JsonProperty("album_id") private String albumId;
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
   * @return The artistId
   */
  @JsonProperty("artist_id") public Integer getArtistId() {
    return artistId;
  }

  /**
   * @param artistId The artist_id
   */
  @JsonProperty("artist_id") public void setArtistId(Integer artistId) {
    this.artistId = artistId;
  }

  /**
   * @return The coverId
   */
  @JsonProperty("cover_id") public Integer getCoverId() {
    return coverId;
  }

  /**
   * @param coverId The cover_id
   */
  @JsonProperty("cover_id") public void setCoverId(Integer coverId) {
    this.coverId = coverId;
  }

  /**
   * @return The albumId
   */
  @JsonProperty("album_id") public String getAlbumId() {
    return albumId;
  }

  /**
   * @param albumId The album_id
   */
  @JsonProperty("album_id") public void setAlbumId(String albumId) {
    this.albumId = albumId;
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
        .append(artistId)
        .append(coverId)
        .append(albumId)
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
    if (!(other instanceof LibraryAlbum)) {
      return false;
    }
    LibraryAlbum rhs = ((LibraryAlbum) other);
    return new EqualsBuilder().append(name, rhs.name)
        .append(artistId, rhs.artistId)
        .append(coverId, rhs.coverId)
        .append(albumId, rhs.albumId)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }
}
