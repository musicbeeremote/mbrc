package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

@Singleton
public class SettingsManager {

  static final String NONE = "none";
  static final String PAUSE = "pause";
  static final String STOP = "stop";
  static final String REDUCE = "reduce";

  private SharedPreferences preferences;
  private Context context;
  private RxBus bus;

  private long settingsId;
  private boolean isFirstRun;

  @Inject
  public SettingsManager(Context context,
                         SharedPreferences preferences,
                         RxBus bus) {
    this.preferences = preferences;
    this.context = context;
    this.bus = bus;
    bus.register(this, ChangeSettings.class, this::handleSettingsChange);

    updatePreferences();

    settingsId = this.preferences.getLong(this.context.getString(R.string.settings_key_default_index), 0);
    checkForFirstRunAfterUpdate();
  }


  public SocketAddress getSocketAddress() {
    String serverAddress = preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    if (isEmpty(serverAddress) || serverPort == 0) {
      bus.post(new DisplayDialog(DisplayDialog.SETUP));
      return null;
    }

    return new InetSocketAddress(serverAddress, serverPort);
  }

  private boolean checkIfRemoteSettingsExist() {
    String serverAddress = preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    return !(isEmpty(serverAddress) || serverPort == 0);
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

  private void updateDefault(ConnectionSettings settings) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(context.getString(R.string.settings_key_hostname), settings.getAddress());
    editor.putInt(context.getString(R.string.settings_key_port), settings.getPort());
    editor.putLong(context.getString(R.string.settings_key_default_index), settings.getId());
    editor.apply();
    settingsId = settings.getId();
  }

  public Date getLastUpdated() {
    return new Date(preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0));
  }

  public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(context.getString(R.string.settings_key_last_update_check), lastChecked.getTime());
    editor.apply();
  }

  @NonNull
  private List<ConnectionSettings> getSettings() {
    return SQLite.select().from(ConnectionSettings.class).queryList();
  }

  private void handleSettingsChange(ChangeSettings event) {
    ConnectionSettings connectionSettings = event.getSettings();
    long id = connectionSettings.getId();
    List<ConnectionSettings> settings = getSettings();
    switch (event.getAction()) {
      case DELETE:
        connectionSettings.delete();
        if (id == settingsId && settings.size() > 0) {
          updateDefault(settings.get(0));
          bus.post(ConnectionSettingsChanged.newInstance(connectionSettings.getId()));
        } else {
          updateDefault(new ConnectionSettings());
        }
        break;
      case DEFAULT:
        updateDefault(connectionSettings);
        bus.post(ConnectionSettingsChanged.newInstance(connectionSettings.getId()));
        break;
      case EDIT:
        connectionSettings.save();
      default:
        break;
    }
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

  @SuppressLint("NewApi")
  private void checkForFirstRunAfterUpdate() {
    try {
      long lastVersionCode = preferences.getLong(context.
          getString(R.string.settings_key_last_version_run), 0);
      long currentVersion = RemoteUtils.getVersionCode(context);

      if (lastVersionCode < currentVersion) {
        isFirstRun = true;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(context.getString(R.string.settings_key_last_version_run), currentVersion);
        editor.apply();

        if (BuildConfig.DEBUG) {
          Timber.d("update or fresh install");
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
