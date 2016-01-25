package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.annimon.stream.Stream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.annotations.SettingsAction;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.domain.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.NotifyUser;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import roboguice.util.Ln;

@Singleton
public class SettingsManager {
  private SharedPreferences preferences;
  private Context context;
  private RxBus bus;
  private ArrayList<ConnectionSettings> mSettings;
  private ObjectMapper mapper;
  private int defaultIndex;
  private boolean isFirstRun;

  @Inject
  public SettingsManager(Context context, SharedPreferences preferences, RxBus bus, ObjectMapper mapper) {
    this.preferences = preferences;
    this.context = context;
    this.bus = bus;
    this.mapper = mapper;
    bus.register(ConnectionSettings.class, this::handleConnectionSettings, false);

    String storedSettings = preferences.getString(context.getString(R.string.settings_key_array), null);
    mSettings = new ArrayList<>();

    if (!TextUtils.isEmpty(storedSettings)) {
      try {
        mSettings = this.mapper.readValue(storedSettings, new TypeReference<List<ConnectionSettings>>() {
        });
        final int[] counter = new int[1];
        Stream.of(mSettings).forEach(value -> value.updateIndex(counter[0]++));
      } catch (IOException e) {
        if (BuildConfig.DEBUG) {
          Ln.d(e, "Loading settings.");
        }
      }
    }
    defaultIndex = this.preferences.getInt(this.context.getString(R.string.settings_key_default_index), 0);
    checkForFirstRunAfterUpdate();
  }

  public SocketAddress getSocketAddress() {
    String serverAddress =
        preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(
          preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    if (TextUtils.isEmpty(serverAddress) || serverPort == 0) {
      bus.post(new DisplayDialog(DisplayDialog.SETUP));
      return null;
    }

    return new InetSocketAddress(serverAddress, serverPort);
  }

  public ConnectionSettings getDefault() {
    return mSettings.size() > 0 ? mSettings.get(defaultIndex) : new ConnectionSettings();
  }

  private boolean checkIfRemoteSettingsExist() {
    String serverAddress =
        preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(
          preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    return !(TextUtils.isEmpty(serverAddress) || serverPort == 0);
  }

  public boolean isVolumeReducedOnRinging() {
    return preferences.getBoolean(context.getString(R.string.settings_key_reduce_volume), false);
  }

  public boolean isNotificationControlEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_notification_control),
        true);
  }

  public boolean isPluginUpdateCheckEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_plugin_check), false);
  }


  private void storeSettings() { //NOPMD
    SharedPreferences.Editor editor = preferences.edit();
    try {
      editor.putString(context.getString(R.string.settings_key_array),
          mapper.writeValueAsString(mSettings));
      editor.apply();

      bus.post(new ConnectionSettingsChanged(mSettings, 0));
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Ln.d(e, "Settings store");
      }
    }
  }

  public void handleConnectionSettings(ConnectionSettings settings) {
    if (settings.getIndex() < 0) {
      if (!mSettings.contains(settings)) {
        if (mSettings.size() == 0) {
          updateDefault(0, settings);
          bus.post(MessageEvent.newInstance(UserInputEventType.SettingsChanged));
        }
        Collections.sort(mSettings);
        int maxElementIndex = mSettings.size() - 1;
        int settingsIndex = 0;
        if (maxElementIndex >= 0) {
          settingsIndex = mSettings.get(maxElementIndex).getIndex() + 1;
        }
        settings.updateIndex(settingsIndex);
        mSettings.add(settings);
        storeSettings();
      } else {
        bus.post(new NotifyUser(R.string.notification_settings_stored));
      }
    } else {
      Collections.sort(mSettings);
      mSettings.set(settings.getIndex(), settings);
      if (settings.getIndex() == defaultIndex) {
        bus.post(MessageEvent.newInstance(UserInputEventType.SettingsChanged));
      }
      storeSettings();
    }
  }


  private void updateDefault(int index, ConnectionSettings settings) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(context.getString(R.string.settings_key_hostname), settings.getAddress());
    editor.putInt(context.getString(R.string.settings_key_port), settings.getPort());
    editor.putInt(context.getString(R.string.settings_key_default_index), index);
    editor.apply();
    defaultIndex = index;
  }

  public Date getLastUpdated() {
    return new Date(
        preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0));
  }

  @SuppressLint("NewApi")
  public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(context.getString(R.string.settings_key_last_update_check),
        lastChecked.getTime());
    editor.apply();
  }


  public ConnectionSettingsChanged produceConnectionSettings() {
    return new ConnectionSettingsChanged(mSettings, defaultIndex);
  }


  public void handleSettingsChange(ChangeSettings event) {
    switch (event.getAction()) {
      case SettingsAction.DELETE:
        mSettings.remove(event.getSettings());
        if (event.getSettings().getIndex() == defaultIndex && mSettings.size() > 0) {
          updateDefault(0, mSettings.get(0));
          bus.post(MessageEvent.newInstance(UserInputEventType.SettingsChanged));
        } else {
          updateDefault(0, new ConnectionSettings());
        }
        storeSettings();
        break;
      case SettingsAction.DEFAULT:
        ConnectionSettings settings = mSettings.get(event.getSettings().getIndex());
        updateDefault(event.getSettings().getIndex(), settings);
        bus.post(new ConnectionSettingsChanged(mSettings, event.getSettings().getIndex()));
        bus.post(MessageEvent.newInstance(UserInputEventType.SettingsChanged));
        break;
      default:
        break;
    }
  }

  public SearchDefaultAction produceAction() {
    return new SearchDefaultAction(
        preferences.getString(context.getString(R.string.settings_search_default_key),
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
          editor.apply();
        } else {
          editor.apply();
        }

        if (BuildConfig.DEBUG) {
          Ln.d("load or fresh install");
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      if (BuildConfig.DEBUG) {
        Ln.d(e, "check for first run");
      }
    }
  }
}
