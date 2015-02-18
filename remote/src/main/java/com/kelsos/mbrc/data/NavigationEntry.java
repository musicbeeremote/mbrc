package com.kelsos.mbrc.data;

import android.support.annotation.DrawableRes;

public class NavigationEntry {
    private String label;

    @DrawableRes
    private int drawableId;

    public NavigationEntry(String label, @DrawableRes int drawableId) {
        this.label = label;
        this.drawableId = drawableId;
    }

    public String getLabel() {
        return label;
    }

    @DrawableRes
    public int getDrawableId() {
        return this.drawableId;
    }

}
