package com.kelsos.mbrc.rest.responses;

public class SuccessResponse {
    private boolean success;

    public SuccessResponse(boolean success) {
        this.success = success;
    }

    public SuccessResponse(){}

    public boolean isSuccess() {
        return success;
    }
}
