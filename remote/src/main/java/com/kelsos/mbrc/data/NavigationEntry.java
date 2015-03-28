package com.kelsos.mbrc.data;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class NavigationEntry {
  @StringRes private int stringId;

  @DrawableRes private int drawableId;

  public NavigationEntry(@StringRes int stringId, @DrawableRes int drawableId) {
    this.stringId = stringId;
    this.drawableId = drawableId;
  }

  @StringRes public int getTitleId() {
    return stringId;
  }

  @DrawableRes public int getDrawableId() {
    return this.drawableId;
  }
}
