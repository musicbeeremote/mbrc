package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;

public class ConnectionSettings {
    private String address;
    private String name;
    private int port;

    public ConnectionSettings(JsonNode node){
        this.address = node.path("address").asText();
        this.name = node.path("name").asText();
        this.port = node.path("port").asInt();
    }

    public ConnectionSettings(String address, String name, int port) {
        this.address = address;
        this.name = name;
        this.port = port;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }
}
