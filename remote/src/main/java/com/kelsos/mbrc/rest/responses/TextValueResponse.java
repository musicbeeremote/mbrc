package com.kelsos.mbrc.rest.responses;

public class TextValueResponse {
    private String value;

    public TextValueResponse(String value) {
        this.value = value;
    }

    public TextValueResponse() {}


    public String getValue() {
        return value;
    }
}
