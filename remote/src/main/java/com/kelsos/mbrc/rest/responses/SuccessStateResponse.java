package com.kelsos.mbrc.rest.responses;

public class SuccessStateResponse extends SuccessResponse{
    private boolean enabled;

    public SuccessStateResponse(boolean success, boolean enabled) {
        super(success);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
