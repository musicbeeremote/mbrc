package com.kelsos.mbrc.helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.kelsos.mbrc.R;
import javax.inject.Inject;

class BasicSettingsHelper {

  @Inject SharedPreferences preferences;
  @Inject Context context;

  String getDefaultAction() {
    final String key = context.getString(R.string.settings_search_default_key);
    final String defaultValue = context.getString(R.string.search_click_default_value);
    return preferences.getString(key, defaultValue);
  }
}
