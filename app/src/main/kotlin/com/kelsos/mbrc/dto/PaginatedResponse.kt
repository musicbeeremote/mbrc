package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("data", "total", "limit", "offset")
class PaginatedResponse<T> : BaseResponse() {

    @JsonProperty("data") var data: List<T> = ArrayList()
    @JsonProperty("total") var total: Int = 0
    @JsonProperty("limit") var limit: Int = 0
    @JsonProperty("offset") var offset: Int = 0
}
