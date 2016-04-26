package com.kelsos.mbrc.dto

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.HashMap

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("context", "address", "name", "port", "http")
class DiscoveryResponse {

    /**
     * @return The context
     */
    /**
     * @param context The context
     */
    @JsonProperty("context") var context: String? = null
    /**
     * @return The address
     */
    /**
     * @param address The address
     */
    @JsonProperty("address") var address: String? = null
    /**
     * @return The name
     */
    /**
     * @param name The name
     */
    @JsonProperty("name") var name: String? = null
    /**
     * @return The port
     */
    /**
     * @param port The port
     */
    @JsonProperty("port") var port: Int = 0
    /**
     * @return The http
     */
    /**
     * @param http The http
     */
    @JsonProperty("http") var http: Int = 0
    @JsonIgnore private val additionalProperties = HashMap<String, Any>()

    override fun toString(): String {
        return "DiscoveryResponse{address='$address\', name='$name\', port=$port, http=$http}"
    }

    @JsonAnyGetter fun getAdditionalProperties(): Map<String, Any> {
        return this.additionalProperties
    }

    @JsonAnySetter fun setAdditionalProperty(name: String, value: Any) {
        this.additionalProperties.put(name, value)
    }
}
