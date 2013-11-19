package com.kelsos.mbrc.data;

import org.codehaus.jackson.annotate.JsonProperty;

public class Queue {
    @JsonProperty private String type;
    @JsonProperty private String query;

    public Queue(String type, String query) {
        this.type = type;
        this.query = query;
    }

    public String getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }
}
