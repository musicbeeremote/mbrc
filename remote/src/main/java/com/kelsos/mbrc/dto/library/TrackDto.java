package com.kelsos.mbrc.dto.library;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "title",
    "position",
    "disc",
    "genre_id",
    "artist_id",
    "album_artist_id",
    "album_id",
    "year",
    "path",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class TrackDto {

  @JsonProperty("title") private String title;
  @JsonProperty("position") private int position;
  @JsonProperty("disc") private int disc;
  @JsonProperty("genre_id") private int genreId;
  @JsonProperty("artist_id") private int artistId;
  @JsonProperty("album_artist_id") private int albumArtistId;
  @JsonProperty("album_id") private int albumId;
  @JsonProperty("year") private String year;
  @JsonProperty("path") private String path;
  @JsonProperty("id") private int id;
  @JsonProperty("date_added") private long dateAdded;
  @JsonProperty("date_updated") private long dateUpdated;
  @JsonProperty("date_deleted") private long dateDeleted;

  /**
   * @return The title
   */
  @JsonProperty("title") public String getTitle() {
    return title;
  }

  /**
   * @param title The title
   */
  @JsonProperty("title") public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return The position
   */
  @JsonProperty("position") public int getPosition() {
    return position;
  }

  /**
   * @param position The position
   */
  @JsonProperty("position") public void setPosition(int position) {
    this.position = position;
  }

  /**
   * @return The genreId
   */
  @JsonProperty("genre_id") public int getGenreId() {
    return genreId;
  }

  /**
   * @param genreId The genre_id
   */
  @JsonProperty("genre_id") public void setGenreId(int genreId) {
    this.genreId = genreId;
  }

  /**
   * @return The artistId
   */
  @JsonProperty("artist_id") public int getArtistId() {
    return artistId;
  }

  /**
   * @param artistId The artist_id
   */
  @JsonProperty("artist_id") public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  /**
   * @return The albumArtistId
   */
  @JsonProperty("album_artist_id") public int getAlbumArtistId() {
    return albumArtistId;
  }

  /**
   * @param albumArtistId The album_artist_id
   */
  @JsonProperty("album_artist_id") public void setAlbumArtistId(int albumArtistId) {
    this.albumArtistId = albumArtistId;
  }

  /**
   * @return The albumId
   */
  @JsonProperty("album_id") public int getAlbumId() {
    return albumId;
  }

  /**
   * @param albumId The album_id
   */
  @JsonProperty("album_id") public void setAlbumId(int albumId) {
    this.albumId = albumId;
  }

  /**
   * @return The year
   */
  @JsonProperty("year") public String getYear() {
    return year;
  }

  /**
   * @param year The year
   */
  @JsonProperty("year") public void setYear(String year) {
    this.year = year;
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
    return new HashCodeBuilder().append(title)
        .append(position)
        .append(genreId)
        .append(artistId)
        .append(albumArtistId)
        .append(albumId)
        .append(year)
        .append(path)
        .append(id)
        .append(dateAdded)
        .append(dateUpdated)
        .append(dateDeleted)
        .append(disc)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof TrackDto)) {
      return false;
    }
    TrackDto rhs = ((TrackDto) other);
    return new EqualsBuilder().append(title, rhs.title)
        .append(position, rhs.position)
        .append(genreId, rhs.genreId)
        .append(artistId, rhs.artistId)
        .append(albumArtistId, rhs.albumArtistId)
        .append(albumId, rhs.albumId)
        .append(disc, rhs.disc)
        .append(year, rhs.year)
        .append(path, rhs.path)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }

  @JsonProperty("disc") public int getDisc() {
    return disc;
  }

  @JsonProperty("disc") public void setDisc(int disc) {
    this.disc = disc;
  }
}
