package com.kelsos.mbrc.data;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({ "index" })
public class ConnectionSettings {
    private String address;
    private String name;
    private int port;
    private int index;

    public ConnectionSettings(JsonNode node) {
        this.address = node.path("address").asText();
        this.name = node.path("name").asText();
        this.port = node.path("port").asInt();
        this.index = -1;
    }

    public ConnectionSettings(String address, String name, int port, int index) {
        this.address = address;
        this.name = name;
        this.port = port;
        this.index = index;
    }

    public ConnectionSettings() {
        this.address = "";
        this.index = -1;
        this.port = 0;
        this.name = "";
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

    @Override public boolean equals(Object o) {
        boolean equality = false;

        if (o instanceof ConnectionSettings) {
            ConnectionSettings other = (ConnectionSettings) o;
            equality = other.getAddress().equals(address) && other.getPort() == port;
        }
        return equality;
    }

    @Override public int hashCode() {
        int hash = 0x192;
        hash = hash * 17 + port;
        hash = hash * 31 + address.hashCode();
        return hash;
    }

    public int getIndex() {
        return index;
    }
}
