package com.kelsos.mbrc.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.events.ui.SettingsChange;
import com.kelsos.mbrc.rest.RemoteEndPoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import roboguice.util.Ln;
import rx.schedulers.Schedulers;

import static com.kelsos.mbrc.util.RemoteUtils.isNullOrEmpty;

@Singleton public class SettingsManager {

  private RemoteEndPoint endPoint;
  private SharedPreferences mPreferences;
  private Context mContext;
  private List<ConnectionSettings> mSettings;
  private ObjectMapper mMapper;
  private int defaultIndex;

  @Inject
  public SettingsManager(Context context, SharedPreferences preferences, ObjectMapper mapper,
      RemoteEndPoint endPoint) {
    this.mPreferences = preferences;
    this.mContext = context;
    this.mMapper = mapper;

    String sVal = preferences.getString(context.getString(R.string.settings_key_array), null);
    mSettings = new ArrayList<>();

    if (!isNullOrEmpty(sVal)) {
      try {
        readConnectionSettings(sVal);
      } catch (IOException e) {
        Ln.d(e);
      }
    }
    defaultIndex = mPreferences.getInt(mContext.getString(R.string.settings_key_default_index), 0);
    Events.connectionSettingsSub.onNext(new ConnectionSettingsChanged(mSettings, defaultIndex));
    checkForFirstRunAfterUpdate();
    Events.settingsChangeSub.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(this::handleSettingsChange, Logger::logThrowable);

    final String host =
        mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
    final int httpPort = mPreferences.getInt(mContext.getString(R.string.settings_key_http), 0);

    this.endPoint = endPoint;
    this.endPoint.setConnectionSettings(host, httpPort);
  }

  private static boolean nullOrEmpty(String string) {
    final String empty = "";
    return string == null || empty.equals(string);
  }

  private void readConnectionSettings(String sVal) throws IOException {
    ArrayNode node;
    node = mMapper.readValue(sVal, ArrayNode.class);
    for (int i = 0; i < node.size(); i++) {
      JsonNode jNode = node.get(i);
      ConnectionSettings settings = new ConnectionSettings(jNode, i);
      mSettings.add(settings);
    }
  }

  public SocketAddress getSocketAddress() {
    String serverAddress =
        mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
    int serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);

    /**
     * Getting Debug Configuration.
     */
    if (BuildConfig.DEBUG) {
      serverAddress = BuildConfig.DEVHOST;
      serverPort = BuildConfig.DEVSOCKPORT;
    }

    if (nullOrEmpty(serverAddress) || serverPort == 0) {
      //TODO: show dialog for setup
      return null;
    }

    return new InetSocketAddress(serverAddress, serverPort);
  }

  public boolean isVolumeReducedOnRinging() {
    return mPreferences.getBoolean(mContext.getString(R.string.settings_key_reduce_volume), false);
  }

  public boolean isNotificationControlEnabled() {
    return mPreferences.getBoolean(mContext.getString(R.string.settings_key_notification_control),
        true);
  }

  private void storeSettings() {
    SharedPreferences.Editor editor = mPreferences.edit();
    try {
      editor.putString(mContext.getString(R.string.settings_key_array),
          mMapper.writeValueAsString(mSettings));
      editor.apply();
      Events.connectionSettingsSub.onNext(new ConnectionSettingsChanged(mSettings, 0));
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Ln.e(e, "Settings store");
      }
    }
  }

  private void addNewSettings(ConnectionSettings settings) {
    if (settings.getIndex() < 0) {
      if (!mSettings.contains(settings)) {
        if (mSettings.size() == 0) {
          updateDefault(0, settings);
          Events.messages.onNext(new Message(EventType.SETTINGS_CHANGED));
        }
        mSettings.add(settings);
        storeSettings();
      } else {
        Events.userMessageSub.onNext(new NotifyUser(R.string.notification_settings_stored));
      }
    } else {
      mSettings.set(settings.getIndex(), settings);
      if (settings.getIndex() == defaultIndex) {
        Events.messages.onNext(new Message(EventType.SETTINGS_CHANGED));
      }
      storeSettings();
    }
  }

  private void updateDefault(int index, ConnectionSettings settings) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putString(mContext.getString(R.string.settings_key_hostname), settings.getAddress());
    editor.putInt(mContext.getString(R.string.settings_key_port), settings.getPort());
    editor.putInt(mContext.getString(R.string.settings_key_default_index), index);
    editor.putInt(mContext.getString(R.string.settings_key_http), settings.getHttp());
    editor.apply();
    defaultIndex = index;
    endPoint.setConnectionSettings(settings.getAddress(), settings.getHttp());
  }

  public Date getLastUpdated() {
    return new Date(
        mPreferences.getLong(mContext.getString(R.string.settings_key_last_update_check), 0));
  }

  public void setLastUpdated(Date lastChecked) {
    SharedPreferences.Editor editor = mPreferences.edit();
    editor.putLong(mContext.getString(R.string.settings_key_last_update_check),
        lastChecked.getTime());
    editor.apply();
  }

  public void handleSettingsChange(SettingsChange event) {
    final ConnectionSettings settings = event.getSettings();
    final int settingsIndex = settings.getIndex();
    switch (event.getAction()) {
      case DELETE:
        mSettings.remove(settingsIndex);
        if (settingsIndex == defaultIndex && mSettings.size() > 0) {
          updateDefault(0, mSettings.get(0));
        } else {
          updateDefault(0, new ConnectionSettings());
        }
        storeSettings();
        break;
      case EDIT:
        mSettings.set(settingsIndex, settings);
        break;
      case DEFAULT:
        updateDefault(settingsIndex, settings);
        break;
      case NEW:
        addNewSettings(settings);
        break;
      default:
        break;
    }
    Events.connectionSettingsSub.onNext(new ConnectionSettingsChanged(mSettings, settingsIndex));
  }

  private void checkForFirstRunAfterUpdate() {
    long lastVersionCode = mPreferences.getLong(mContext.
        getString(R.string.settings_key_last_version_run), 0);
    long currentVersion = BuildConfig.VERSION_CODE;

    if (lastVersionCode < currentVersion) {

      SharedPreferences.Editor editor = mPreferences.edit();
      editor.putLong(mContext.getString(R.string.settings_key_last_version_run), currentVersion);
      editor.apply();

      if (BuildConfig.DEBUG) {
        Ln.d("update or fresh install");
      }
    }
  }
}
