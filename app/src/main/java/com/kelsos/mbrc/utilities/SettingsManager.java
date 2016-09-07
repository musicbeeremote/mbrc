package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.StringDef;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class SettingsManager {

  static final String NONE = "none";
  static final String PAUSE = "pause";
  static final String STOP = "stop";
  static final String REDUCE = "reduce";

  private SharedPreferences preferences;
  private Context context;

  @Inject
  public SettingsManager(Application application, SharedPreferences preferences) {
    this.preferences = preferences;
    this.context = application;

    updatePreferences();

    checkForFirstRunAfterUpdate();
  }

  private void updatePreferences() {
    boolean enabled = preferences.getBoolean(context.getString(R.string.settings_legacy_key_reduce_volume), false);
    if (enabled) {
      preferences.edit().putString(context.getString(R.string.settings_key_incoming_call_action), REDUCE).apply();
    }
  }

  public boolean isNotificationControlEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_notification_control), true);
  }

  @SuppressWarnings("WrongConstant")
  @SettingsManager.CallAction
  String getCallAction() {
    return preferences.getString(context.getString(R.string.settings_key_incoming_call_action), NONE);
  }

  public boolean isPluginUpdateCheckEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_plugin_check), false);
  }

  public Date getLastUpdated() {
    return new Date(preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0));
  }

  public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(context.getString(R.string.settings_key_last_update_check), lastChecked.getTime());
    editor.apply();
  }

  @SuppressLint("NewApi")
  private void checkForFirstRunAfterUpdate() {
    try {
      long lastVersionCode = preferences.getLong(context.
          getString(R.string.settings_key_last_version_run), 0);
      long currentVersion = RemoteUtils.getVersionCode(context);

      if (lastVersionCode < currentVersion) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.settings_key_last_version_run), currentVersion);
        editor.apply();

        if (BuildConfig.DEBUG) {
          Timber.d("save or fresh install");
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      if (BuildConfig.DEBUG) {
        Timber.d(e, "check for first run");
      }
    }
  }

  @StringDef({
      NONE,
      PAUSE,
      STOP
  })
  @Retention(RetentionPolicy.SOURCE)
  @interface CallAction {

  }
}
