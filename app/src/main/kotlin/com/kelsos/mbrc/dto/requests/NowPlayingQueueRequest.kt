package com.kelsos.mbrc.dto.requests

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.MetaDataType.Type
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.extensions.empty

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("type", "action", "id", "path")
class NowPlayingQueueRequest {

    @Type @JsonProperty("type") var type: String = MetaDataType.UNDEF
    @Action @JsonProperty("action") var action: String = Queue.NOW
    @JsonProperty("id") var id: Long = 0
    @JsonProperty("path") var path: String = String.empty
}

