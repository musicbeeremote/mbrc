package com.kelsos.mbrc.dto.player

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.dto.BaseResponse

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder("repeat", "mute", "shuffle", "scrobble", "state", "volume")
class PlayerStatusResponse : BaseResponse() {

    /**
     * @return The repeat
     */
    /**
     * @param repeat The repeat
     */
    @JsonProperty("repeat") var repeat: String? = null
    /**
     * @return The mute
     */
    /**
     * @param mute The mute
     */
    @JsonProperty("mute") var mute: Boolean? = null
    /**
     * @return The shuffle
     */
    /**
     * @param shuffle The shuffle
     */
    @JsonProperty("shuffle") var shuffle: String? = null
    /**
     * @return The scrobble
     */
    /**
     * @param scrobble The scrobble
     */
    @JsonProperty("scrobble") var scrobble: Boolean? = null
    /**
     * @return The state
     */
    /**
     * @param state The state
     */
    @JsonProperty("state") var state: String? = null
    /**
     * @return The volume
     */
    /**
     * @param volume The volume
     */
    @JsonProperty("volume") var volume: Int? = null
}
