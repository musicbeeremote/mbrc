package com.kelsos.mbrc.data;

import android.graphics.drawable.Drawable;

public class NavigationEntry {
    private String label;
    private Drawable leftDrawable;

    public NavigationEntry(String label, Drawable leftDrawable, int w, int h) {
        this.label = label;
        this.leftDrawable = leftDrawable;
        this.leftDrawable.setBounds(0, 0, w, h);
    }

    public Drawable getLeftDrawable() {
        return leftDrawable;
    }

    public String getLabel() {
        return label;
    }
}
