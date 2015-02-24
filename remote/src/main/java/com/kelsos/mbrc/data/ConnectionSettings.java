package com.kelsos.mbrc.data;

import android.support.annotation.NonNull;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"index"})
public class ConnectionSettings implements Comparable<ConnectionSettings> {
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

    public void updateIndex(int index) {
        this.index = index;
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



    public int getIndex() {
        return index;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override public int compareTo(@NonNull ConnectionSettings another) {
        int compare = 0;

        if (index < another.getIndex()) {
            compare = -1;
        } else if (index > another.getIndex()) {
            compare = 1;
        }
        return  compare;
    }
}
