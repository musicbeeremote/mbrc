package com.kelsos.mbrc.data;

import android.graphics.drawable.Drawable;

public class NavigationEntry {
    private String label;
    private Drawable leftDrawable;

    public NavigationEntry(String label, Drawable leftDrawable) {
        this.label = label;
        this.leftDrawable = leftDrawable;
        this.leftDrawable.setBounds(0,0,40,40);
    }

    public Drawable getLeftDrawable() {
        return leftDrawable;
    }

    public String getLabel() {
        return label;
    }
}
