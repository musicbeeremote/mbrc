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
})
public class Track {

    @JsonProperty("title")
    private String title;
    @JsonProperty("position")
    private Integer position;
    @JsonProperty("genre_id")
    private Integer genreId;
    @JsonProperty("artist_id")
    private Integer artistId;
    @JsonProperty("album_artist_id")
    private Integer albumArtistId;
    @JsonProperty("album_id")
    private Integer albumId;
    @JsonProperty("year")
    private String year;
    @JsonProperty("path")
    private String path;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("date_added")
    private String dateAdded;
    @JsonProperty("date_updated")
    private String dateUpdated;
    @JsonProperty("date_deleted")
    private String dateDeleted;

    /**
     *
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     *     The position
     */
    @JsonProperty("position")
    public Integer getPosition() {
        return position;
    }

    /**
     *
     * @param position
     *     The position
     */
    @JsonProperty("position")
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     *
     * @return
     *     The genreId
     */
    @JsonProperty("genre_id")
    public Integer getGenreId() {
        return genreId;
    }

    /**
     *
     * @param genreId
     *     The genre_id
     */
    @JsonProperty("genre_id")
    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    /**
     *
     * @return
     *     The artistId
     */
    @JsonProperty("artist_id")
    public Integer getArtistId() {
        return artistId;
    }

    /**
     *
     * @param artistId
     *     The artist_id
     */
    @JsonProperty("artist_id")
    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    /**
     *
     * @return
     *     The albumArtistId
     */
    @JsonProperty("album_artist_id")
    public Integer getAlbumArtistId() {
        return albumArtistId;
    }

    /**
     *
     * @param albumArtistId
     *     The album_artist_id
     */
    @JsonProperty("album_artist_id")
    public void setAlbumArtistId(Integer albumArtistId) {
        this.albumArtistId = albumArtistId;
    }

    /**
     *
     * @return
     *     The albumId
     */
    @JsonProperty("album_id")
    public Integer getAlbumId() {
        return albumId;
    }

    /**
     *
     * @param albumId
     *     The album_id
     */
    @JsonProperty("album_id")
    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    /**
     *
     * @return
     *     The year
     */
    @JsonProperty("year")
    public String getYear() {
        return year;
    }

    /**
     *
     * @param year
     *     The year
     */
    @JsonProperty("year")
    public void setYear(String year) {
        this.year = year;
    }

    /**
     *
     * @return
     *     The path
     */
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    /**
     *
     * @param path
     *     The path
     */
    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    /**
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The dateAdded
     */
    @JsonProperty("date_added")
    public String getDateAdded() {
        return dateAdded;
    }

    /**
     *
     * @param dateAdded
     *     The date_added
     */
    @JsonProperty("date_added")
    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     *
     * @return
     *     The dateUpdated
     */
    @JsonProperty("date_updated")
    public String getDateUpdated() {
        return dateUpdated;
    }

    /**
     *
     * @param dateUpdated
     *     The date_updated
     */
    @JsonProperty("date_updated")
    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    /**
     *
     * @return
     *     The dateDeleted
     */
    @JsonProperty("date_deleted")
    public String getDateDeleted() {
        return dateDeleted;
    }

    /**
     *
     * @param dateDeleted
     *     The date_deleted
     */
    @JsonProperty("date_deleted")
    public void setDateDeleted(String dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(title).append(position).append(genreId).append(artistId).append(albumArtistId).append(albumId).append(year).append(path).append(id).append(dateAdded).append(dateUpdated).append(dateDeleted).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Track) == false) {
            return false;
        }
        Track rhs = ((Track) other);
        return new EqualsBuilder().append(title, rhs.title).append(position, rhs.position).append(genreId, rhs.genreId).append(artistId, rhs.artistId).append(albumArtistId, rhs.albumArtistId).append(albumId, rhs.albumId).append(year, rhs.year).append(path, rhs.path).append(id, rhs.id).append(dateAdded, rhs.dateAdded).append(dateUpdated, rhs.dateUpdated).append(dateDeleted, rhs.dateDeleted).isEquals();
    }

}
