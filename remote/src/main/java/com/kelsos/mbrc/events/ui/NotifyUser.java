package com.kelsos.mbrc.events.ui;

public class NotifyUser {
    private String message;
    private int resId;
    private boolean isFromResource;

    public NotifyUser(String message) {
        this.message = message;
        this.isFromResource = false;
    }

    public NotifyUser(int resId) {
        this.resId = resId;
        this.isFromResource = true;
    }

    public String getMessage() {
        return message;
    }

    public int getResId() {
        return resId;
    }

    public boolean isFromResource() {
        return isFromResource;
    }
}
