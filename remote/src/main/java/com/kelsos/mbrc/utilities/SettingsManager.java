package com.kelsos.mbrc.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import roboguice.util.Ln;

@Singleton public class SettingsManager {
  private SharedPreferences mPreferences;
  private Context mContext;
  private MainThreadBusWrapper bus;
  private ArrayList<ConnectionSettings> mSettings;
  private ObjectMapper mMapper;
  private int defaultIndex;
  private boolean isFirstRun;

  @Inject
  public SettingsManager(Context context, SharedPreferences preferences, MainThreadBusWrapper bus,
      ObjectMapper mapper) {
    this.mPreferences = preferences;
    this.mContext = context;
    this.bus = bus;
    this.mMapper = mapper;
    bus.register(this);

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
          Ln.d(e, "Loading settings.");
        }
      }
    }
    defaultIndex = mPreferences.getInt(mContext.getString(R.string.settings_key_default_index), 0);
    checkForFirstRunAfterUpdate();
  }

  private static boolean nullOrEmpty(String string) {
    return string == null || Const.EMPTY.equals(string);
  }

  public SocketAddress getSocketAddress() {
    String serverAddress =
        mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(
          mPreferences.getString(mContext.getString(R.string.settings_key_port), "0"));
    }

    if (nullOrEmpty(serverAddress) || serverPort == 0) {
      bus.post(new DisplayDialog(DisplayDialog.SETUP));
      return null;
    }

    return new InetSocketAddress(serverAddress, serverPort);
  }

  private boolean checkIfRemoteSettingsExist() {
    String serverAddress =
        mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
    int serverPort;

    try {
      serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);
    } catch (ClassCastException castException) {
      serverPort = Integer.parseInt(
          mPreferences.getString(mContext.getString(R.string.settings_key_port), "0"));
    }

    return !(nullOrEmpty(serverAddress) || serverPort == 0);
  }

  public boolean isVolumeReducedOnRinging() {
    return mPreferences.getBoolean(mContext.getString(R.string.settings_key_reduce_volume), false);
  }

  public boolean isNotificationControlEnabled() {
    return mPreferences.getBoolean(mContext.getString(R.string.settings_key_notification_control),
        true);
  }

  @SuppressLint("NewApi") private void storeSettings() { //NOPMD
    SharedPreferences.Editor editor = mPreferences.edit();
    try {
      editor.putString(mContext.getString(R.string.settings_key_array),
          mMapper.writeValueAsString(mSettings));

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        editor.apply();
      } else {
        editor.commit();
      }

      bus.post(new ConnectionSettingsChanged(mSettings, 0));
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Ln.d(e, "Settings store");
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

  @SuppressLint("NewApi") private void updateDefault(int index, ConnectionSettings settings) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(mContext.getString(R.string.settings_key_hostname), settings.getAddress());
    editor.putInt(mContext.getString(R.string.settings_key_port), settings.getPort());
    editor.putInt(mContext.getString(R.string.settings_key_default_index), index);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }
    defaultIndex = index;
  }

  public Date getLastUpdated() {
    return new Date(
        mPreferences.getLong(mContext.getString(R.string.settings_key_last_update_check), 0));
  }

  @SuppressLint("NewApi") public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putLong(mContext.getString(R.string.settings_key_last_update_check),
        lastChecked.getTime());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      editor.apply();
    } else {
      editor.commit();
    }
  }

  @Produce public ConnectionSettingsChanged produceConnectionSettings() {
    return new ConnectionSettingsChanged(mSettings, defaultIndex);
  }

  @Subscribe public void handleSettingsChange(ChangeSettings event) {
    switch (event.getAction()) {
      case DELETE:
        mSettings.remove(event.getSettings());
        if (event.getSettings().getIndex() == defaultIndex && mSettings.size() > 0) {
          updateDefault(0, mSettings.get(0));
          bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
        } else {
          updateDefault(0, new ConnectionSettings());
        }
        storeSettings();
        break;
      case DEFAULT:
        ConnectionSettings settings = mSettings.get(event.getSettings().getIndex());
        updateDefault(event.getSettings().getIndex(), settings);
        bus.post(new ConnectionSettingsChanged(mSettings, event.getSettings().getIndex()));
        bus.post(new MessageEvent(UserInputEventType.SettingsChanged));
        break;
      default:
        break;
    }
  }

  @Produce public SearchDefaultAction produceAction() {
    return new SearchDefaultAction(
        mPreferences.getString(mContext.getString(R.string.settings_search_default_key),
            mContext.getString(R.string.search_click_default_value)));
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
      long lastVersionCode = mPreferences.getLong(mContext.
          getString(R.string.settings_key_last_version_run), 0);
      long currentVersion = RemoteUtils.getVersionCode(mContext);

      if (lastVersionCode < currentVersion) {
        isFirstRun = true;

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(mContext.getString(R.string.settings_key_last_version_run), currentVersion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
          editor.apply();
        } else {
          editor.commit();
        }

        if (BuildConfig.DEBUG) {
          Ln.d("update or fresh install");
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      if (BuildConfig.DEBUG) {
        Ln.d(e, "check for first run");
      }
    }
  }
}
