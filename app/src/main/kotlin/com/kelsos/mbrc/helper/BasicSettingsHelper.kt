package com.kelsos.mbrc.helper

import android.content.SharedPreferences
import android.content.res.Resources
import com.kelsos.mbrc.R
import javax.inject.Inject

class BasicSettingsHelper
@Inject
constructor(private val preferences: SharedPreferences, private var resources: Resources) {

  val defaultAction: String
    get() {
      val key = resources.getString(R.string.settings_search_default_key)
      val defaultValue = resources.getString(R.string.search_click_default_value)
      return preferences.getString(key, defaultValue)
    }
}
