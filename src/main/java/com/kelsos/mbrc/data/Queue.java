package com.kelsos.mbrc.data;

public class Queue {
    private String type;
    private String query;

    public Queue(String type, String query) {
        this.type = type;
        this.query = query;
    }

    public String getType() {
        return type;
    };

    public String getQuery() {
        return query;
    }
}
