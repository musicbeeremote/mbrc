package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import java.util.ArrayList

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("data", "total", "limit", "offset")
class PaginatedResponse<T> : BaseResponse() {

    @JsonProperty("data") private var data: List<T> = ArrayList()
    @JsonProperty("total") private var total: Int = 0
    @JsonProperty("limit") private var limit: Int = 0
    @JsonProperty("offset") private var offset: Int = 0

    /**
     * @return The data
     */
    @JsonProperty("data") fun getData(): List<T> {
        return data
    }

    /**
     * @param data The data
     */
    @JsonProperty("data") fun setData(data: List<T>): PaginatedResponse<T> {
        this.data = data
        return this
    }

    /**
     * @return The total
     */
    @JsonProperty("total") fun getTotal(): Int {
        return total
    }

    /**
     * @param total The total
     */
    @JsonProperty("total") fun setTotal(total: Int): PaginatedResponse<T> {
        this.total = total
        return this
    }

    /**
     * @return The limit
     */
    @JsonProperty("limit") fun getLimit(): Int {
        return limit
    }

    /**
     * @param limit The limit
     */
    @JsonProperty("limit") fun setLimit(limit: Int): PaginatedResponse<T> {
        this.limit = limit
        return this
    }

    /**
     * @return The offset
     */
    @JsonProperty("offset") fun getOffset(): Int {
        return offset
    }

    /**
     * @param offset The offset
     */
    @JsonProperty("offset") fun setOffset(offset: Int): PaginatedResponse<T> {
        this.offset = offset
        return this
    }

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(data).append(total).append(limit).append(offset).toHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is PaginatedResponse<Any>) {
            return false
        }
        return EqualsBuilder().append(data, other.data).append(total, other.total).append(limit, other.limit).append(offset, other.offset).isEquals
    }
}
