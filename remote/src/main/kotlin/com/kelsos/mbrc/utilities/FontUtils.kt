package com.kelsos.mbrc.utilities

import android.content.Context
import android.graphics.Typeface

object FontUtils {

  fun getRobotoRegular(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "fonts/roboto_regular.ttf")
  }

  fun getRobotoMedium(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "fonts/roboto_medium.ttf")
  }
}
