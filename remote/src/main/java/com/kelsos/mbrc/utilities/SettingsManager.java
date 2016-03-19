package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.domain.DeviceSettings;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.repository.DeviceRepository;
import java.util.Date;
import rx.Observable;
import timber.log.Timber;

@Singleton public class SettingsManager {
  public static final String DEFAULT_ID = "default_id";
  private SharedPreferences preferences;
  private Context context;
  private boolean isFirstRun;
  private DeviceRepository repository;

  @Inject public SettingsManager(Context context, SharedPreferences preferences, DeviceRepository repository) {
    this.preferences = preferences;
    this.context = context;
    this.repository = repository;
    checkForFirstRunAfterUpdate();
  }

  private boolean checkIfRemoteSettingsExist() {
    String serverAddress = preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    return !(TextUtils.isEmpty(serverAddress) || serverPort == 0);
  }

  public boolean isVolumeReducedOnRinging() {
    return preferences.getBoolean(context.getString(R.string.settings_key_reduce_volume), false);
  }

  public boolean isNotificationControlEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_notification_control), true);
  }

  public boolean isPluginUpdateCheckEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_plugin_check), false);
  }

  private void updateDefault(int index, DeviceSettings settings) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(context.getString(R.string.settings_key_hostname), settings.getAddress());
    editor.putInt(context.getString(R.string.settings_key_port), settings.getPort());
    editor.putInt(context.getString(R.string.settings_key_default_index), index);
    editor.apply();
  }

  public Date getLastUpdated() {
    return new Date(preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0));
  }

  @SuppressLint("NewApi") public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(context.getString(R.string.settings_key_last_update_check), lastChecked.getTime());
    editor.apply();
  }

  public SearchDefaultAction produceAction() {
    return new SearchDefaultAction(preferences.getString(context.getString(R.string.settings_search_default_key),
        context.getString(R.string.search_click_default_value)));
  }

  public DisplayDialog produceDisplayDialog() {
    int run = DisplayDialog.NONE;
    if (isFirstRun && checkIfRemoteSettingsExist()) {
      run = DisplayDialog.UPGRADE;
    } else if (isFirstRun && !checkIfRemoteSettingsExist()) {
      run = DisplayDialog.INSTALL;
    }
    isFirstRun = false;
    return new DisplayDialog(run);
  }

  @SuppressLint("NewApi") private void checkForFirstRunAfterUpdate() {
    try {
      long lastVersionCode = preferences.getLong(context.
          getString(R.string.settings_key_last_version_run), 0);
      long currentVersion = RemoteUtils.getVersionCode(context);

      if (lastVersionCode < currentVersion) {
        isFirstRun = true;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.settings_key_last_version_run), currentVersion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
          editor.apply();
        } else {
          editor.apply();
        }

        if (BuildConfig.DEBUG) {
          Timber.d("load or fresh install");
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      if (BuildConfig.DEBUG) {
        Timber.d(e, "check for first run");
      }
    }
  }

  public Observable<DeviceSettings> getDefault() {
    return Observable.just(preferences.getLong(DEFAULT_ID, -1)).flatMap(id -> Observable.create(subscriber -> {
      if (id > 0) {
        subscriber.onNext(repository.getById(id));
      }
      subscriber.onCompleted();
    }));
  }

  public void setDefault(long id) {
    preferences.edit().putLong(DEFAULT_ID, id).apply();
  }
}
