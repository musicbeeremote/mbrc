package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("type", "action", "id", "path")
class NowPlayingQueueRequest {

    @MetaDataType.Type @JsonProperty("type") val type: String? = null
    @Queue.Action @JsonProperty("action") val action: String? = null
    @JsonProperty("id") val id: Int = 0
    @JsonProperty("path") val path: String? = null
}

