package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {

  public static Typeface getRobotoRegular(Context context) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
  }

  public static Typeface getRobotoMedium(Context context) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
  }
}
