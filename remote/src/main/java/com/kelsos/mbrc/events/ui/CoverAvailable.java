package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;

public class CoverAvailable {
    private Bitmap cover;

    public CoverAvailable(Bitmap cover) {
        this.cover = cover;
    }

    public Bitmap getCover() {
        return cover;
    }
}
