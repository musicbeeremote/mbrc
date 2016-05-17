package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.StringDef;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import timber.log.Timber;

@Singleton public class SettingsManager {

  public static final String NONE = "none";
  public static final String PAUSE = "pause";
  public static final String STOP = "stop";
  public static final String REDUCE = "reduce";

  private SharedPreferences preferences;
  private Context context;
  private MainThreadBusWrapper bus;
  private ArrayList<ConnectionSettings> mSettings;
  private ObjectMapper mMapper;
  private int defaultIndex;
  private boolean isFirstRun;

  @Inject
  public SettingsManager(Context context,
      SharedPreferences preferences,
      MainThreadBusWrapper bus,
      ObjectMapper mapper) {
    this.preferences = preferences;
    this.context = context;
    this.bus = bus;
    this.mMapper = mapper;
    bus.register(this);

    updatePreferences();

    String sVal = preferences.getString(context.getString(R.string.settings_key_array), null);
    mSettings = new ArrayList<>();

    if (sVal != null && !Const.EMPTY.equals(sVal)) {
      ArrayNode node;
      try {
        node = mMapper.readValue(sVal, ArrayNode.class);
        for (int i = 0; i < node.size(); i++) {
          JsonNode jNode = node.get(i);
          ConnectionSettings settings = new ConnectionSettings(jNode);
          settings.updateIndex(i);
          mSettings.add(settings);
        }
      } catch (IOException e) {
        if (BuildConfig.DEBUG) {
          Timber.d(e, "Loading settings.");
        }
      }
    }
    defaultIndex = this.preferences.getInt(this.context.getString(R.string.settings_key_default_index), 0);
    checkForFirstRunAfterUpdate();
  }

  private static boolean nullOrEmpty(String string) {
    return string == null || Const.EMPTY.equals(string);
  }

  public SocketAddress getSocketAddress() {
    String serverAddress = preferences.getString(context.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = preferences.getInt(context.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(preferences.getString(context.getString(R.string.settings_key_port), "0"));
    }

    if (nullOrEmpty(serverAddress) || serverPort == 0) {
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

    return !(nullOrEmpty(serverAddress) || serverPort == 0);
  }

  public void updatePreferences() {
    boolean enabled = preferences.getBoolean(context.getString(R.string.settings_legacy_key_reduce_volume), false);
    if (enabled) {
      preferences.edit().putString(context.getString(R.string.settings_key_incoming_call_action), REDUCE).apply();
    }
  }

  public boolean isNotificationControlEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_notification_control), true);
  }

  @SuppressWarnings("WrongConstant")
  @SettingsManager.CallAction public String getCallAction() {
    return preferences.getString(context.getString(R.string.settings_key_incoming_call_action), NONE);
  }

  public boolean isPluginUpdateCheckEnabled() {
    return preferences.getBoolean(context.getString(R.string.settings_key_plugin_check), false);
  }

  private void storeSettings() { //NOPMD
    SharedPreferences.Editor editor = preferences.edit();
    try {
      editor.putString(context.getString(R.string.settings_key_array), mMapper.writeValueAsString(mSettings)).apply();
      bus.post(new ConnectionSettingsChanged(mSettings, 0));
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Timber.d(e, "Settings store");
      }
    }
  }

  @Subscribe public void handleConnectionSettings(ConnectionSettings settings) {
    if (settings.getIndex() < 0) {
      if (!mSettings.contains(settings)) {
        if (mSettings.size() == 0) {
          updateDefault(0, settings);
          bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
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
        bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
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
    return new Date(preferences.getLong(context.getString(R.string.settings_key_last_update_check), 0));
  }

  public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(context.getString(R.string.settings_key_last_update_check), lastChecked.getTime());
    editor.apply();
  }

  @Produce public ConnectionSettingsChanged produceConnectionSettings() {
    return new ConnectionSettingsChanged(mSettings, defaultIndex);
  }

  @Subscribe public void handleSettingsChange(ChangeSettings event) {
    int index = event.getSettings().getIndex();
    switch (event.getAction()) {
      case DELETE:
        mSettings.remove(event.getSettings());
        if (index == defaultIndex && mSettings.size() > 0) {
          updateDefault(0, mSettings.get(0));
          bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
        } else {
          updateDefault(0, new ConnectionSettings());
        }
        storeSettings();
        break;
      case DEFAULT:
        if (index == mSettings.size()) {
          index -= 1;
        }
        ConnectionSettings settings = mSettings.get(index);
        updateDefault(index, settings);
        bus.post(new ConnectionSettingsChanged(mSettings, index));
        bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
        break;
      default:
        break;
    }
  }

  @Produce public SearchDefaultAction produceAction() {
    return new SearchDefaultAction(preferences.getString(context.getString(R.string.settings_search_default_key),
        context.getString(R.string.search_click_default_value)));
  }

  @Produce public DisplayDialog produceDisplayDialog() {
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
  }) @Retention(RetentionPolicy.SOURCE) public @interface CallAction {

  }
}
