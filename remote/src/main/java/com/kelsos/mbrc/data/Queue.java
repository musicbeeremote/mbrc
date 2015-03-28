package com.kelsos.mbrc.data;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.codehaus.jackson.annotate.JsonProperty;

public class Queue {
  public static final String NEXT = "next";
  public static final String LAST = "last";
  public static final String NOW = "now";
  @JsonProperty private String type;
  @JsonProperty private String query;

  public Queue(@QueueType String type, String query) {
    this.type = type;
    this.query = query;
  }

  @QueueType public String getType() {
    return type;
  }

  @SuppressWarnings("unused") public String getQuery() {
    return query;
  }

  @StringDef({ NEXT, LAST, NOW })
  @Retention(RetentionPolicy.SOURCE) public @interface QueueType { }
}
