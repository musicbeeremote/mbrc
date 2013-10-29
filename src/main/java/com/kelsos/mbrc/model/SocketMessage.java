package com.kelsos.mbrc.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class SocketMessage {
    @JsonProperty private String context;
    @JsonProperty private String type;
    @JsonProperty private Object data;

    public SocketMessage() {
        this.context = "uninitialized";
        this.data = "missing";
        this.type = "error";
    }

    public SocketMessage(String context, String type) {
        this.context = context;
        this.type = type;
        this.data = "";
    }

    public SocketMessage(String context, String type, Object data) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
