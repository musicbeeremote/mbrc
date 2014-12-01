package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;

public class CoverAvailable {
    private boolean isAvailable;
    private Bitmap cover;

    public CoverAvailable(Bitmap cover) {
        this.isAvailable = true;
        this.cover = cover;
    }

    public CoverAvailable() {
        this.isAvailable = false;
        this.cover = null;
    }

    public boolean getIsAvailable() {
        return this.isAvailable;
    }

    public Bitmap getCover(){
        return  this.cover;
    }
}
