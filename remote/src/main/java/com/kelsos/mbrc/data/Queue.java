package com.kelsos.mbrc.data;

import android.support.annotation.StringDef;
import org.codehaus.jackson.annotate.JsonProperty;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Queue {
    @JsonProperty private String type;
    @JsonProperty private String query;

    public static final String NEXT = "next";
    public static final String LAST = "last";
    public static final String NOW = "now";

    @StringDef({NEXT, LAST, NOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface QueueType {}

    public Queue(@QueueType String type, String query) {
        this.type = type;
        this.query = query;
    }

    @QueueType
    public String getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }
}
