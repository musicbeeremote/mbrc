package com.kelsos.mbrc.helper;

import android.content.SharedPreferences;
import android.content.res.Resources;
import com.kelsos.mbrc.R;
import javax.inject.Inject;

class BasicSettingsHelper {

  @Inject SharedPreferences preferences;
  @Inject Resources resources;

  String getDefaultAction() {
    final String key = resources.getString(R.string.settings_search_default_key);
    final String defaultValue = resources.getString(R.string.search_click_default_value);
    return preferences.getString(key, defaultValue);
  }
}
