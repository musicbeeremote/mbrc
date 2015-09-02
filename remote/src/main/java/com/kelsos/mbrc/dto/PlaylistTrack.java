package com.kelsos.mbrc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "track_info_id",
    "playlist_id",
    "position",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
})
public class PlaylistTrack {

    @JsonProperty("track_info_id")
    private Integer trackInfoId;
    @JsonProperty("playlist_id")
    private Integer playlistId;
    @JsonProperty("position")
    private Integer position;
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
     *     The trackInfoId
     */
    @JsonProperty("track_info_id")
    public Integer getTrackInfoId() {
        return trackInfoId;
    }

    /**
     *
     * @param trackInfoId
     *     The track_info_id
     */
    @JsonProperty("track_info_id")
    public void setTrackInfoId(Integer trackInfoId) {
        this.trackInfoId = trackInfoId;
    }

    /**
     *
     * @return
     *     The playlistId
     */
    @JsonProperty("playlist_id")
    public Integer getPlaylistId() {
        return playlistId;
    }

    /**
     *
     * @param playlistId
     *     The playlist_id
     */
    @JsonProperty("playlist_id")
    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
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
        return new HashCodeBuilder().append(trackInfoId).append(playlistId).append(position).append(id).append(dateAdded).append(dateUpdated).append(dateDeleted).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PlaylistTrack) == false) {
            return false;
        }
        PlaylistTrack rhs = ((PlaylistTrack) other);
        return new EqualsBuilder().append(trackInfoId, rhs.trackInfoId).append(playlistId, rhs.playlistId).append(position, rhs.position).append(id, rhs.id).append(dateAdded, rhs.dateAdded).append(dateUpdated, rhs.dateUpdated).append(dateDeleted, rhs.dateDeleted).isEquals();
    }

}
