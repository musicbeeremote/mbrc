package com.kelsos.mbrc.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.BuildConfig;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.UserInputEventType;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.DisplayDialog;
import com.kelsos.mbrc.events.ui.NotifyUser;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import roboguice.util.Ln;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class SettingsManager {
    private SharedPreferences mPreferences;
    private Context mContext;
    private List<ConnectionSettings> mSettings;
    private ObjectMapper mMapper;
    private int defaultIndex;
    private boolean isFirstRun;

    @Inject public SettingsManager(Context context, SharedPreferences preferences,
                                   ObjectMapper mapper) {
        this.mPreferences = preferences;
        this.mContext = context;
        this.mMapper = mapper;

        String sVal = preferences.getString(context.getString(R.string.settings_key_array), null);
        mSettings = new ArrayList<>();

        if (sVal != null && !sVal.equals("")) {
            ArrayNode node;
            try {
                node = mMapper.readValue(sVal, ArrayNode.class);
                for (int i = 0; i < node.size(); i++) {
                    JsonNode jNode = node.get(i);
                    ConnectionSettings settings = new ConnectionSettings(jNode);
                    mSettings.add(settings);
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Ln.e(e, "connection-settings");
                }
            }
        }
        defaultIndex = mPreferences.getInt(mContext.getString(R.string.settings_key_default_index), 0);
        checkForFirstRunAfterUpdate();
    }

    public SocketAddress getSocketAddress() {
        String serverAddress = mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
        int serverPort;

        try {
            serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);
        } catch (ClassCastException castException) {
            serverPort = Integer.parseInt(mPreferences.getString(mContext.getString(R.string.settings_key_port), "0"));
        }


        if (nullOrEmpty(serverAddress) || serverPort == 0) {
            new DisplayDialog(DisplayDialog.SETUP);
            return null;
        }

        return new InetSocketAddress(serverAddress, serverPort);
    }

    private boolean checkIfRemoteSettingsExist() {
        String serverAddress = mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
        int serverPort;

        try {
            serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);
        } catch (ClassCastException castException) {
            serverPort = Integer.parseInt(mPreferences.getString(mContext.getString(R.string.settings_key_port), "0"));
        }

        return !(nullOrEmpty(serverAddress) || serverPort == 0);
    }

    public boolean isVolumeReducedOnRinging() {
        return mPreferences.getBoolean(mContext.getString(R.string.settings_key_reduce_volume), false);
    }

    private static boolean nullOrEmpty(String string) {
        return string == null || string.equals("");
    }

    public boolean isNotificationControlEnabled() {
        return mPreferences.getBoolean(mContext.getString(R.string.settings_key_notification_control), true);
    }

    private void storeSettings() {
        SharedPreferences.Editor editor = mPreferences.edit();
        try {
            editor.putString(mContext.getString(R.string.settings_key_array), mMapper.writeValueAsString(mSettings));
            editor.apply();
            new ConnectionSettingsChanged(mSettings, 0);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Ln.e(e, "Settings store");
            }
        }
    }

    public void handleConnectionSettings(ConnectionSettings settings) {
        if (settings.getIndex() < 0) {
            if (!mSettings.contains(settings)) {
                if (mSettings.size() == 0) {
                    updateDefault(0, settings);
                    new MessageEvent(UserInputEventType.SETTINGS_CHANGED);
                }
                mSettings.add(settings);
                storeSettings();
            } else {
                new NotifyUser(R.string.notification_settings_stored);
            }
        } else {
            mSettings.set(settings.getIndex(), settings);
            if (settings.getIndex() == defaultIndex) {
                new MessageEvent(UserInputEventType.SETTINGS_CHANGED);
            }
            storeSettings();
        }
    }

    private void updateDefault(int index, ConnectionSettings settings) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(mContext.getString(R.string.settings_key_hostname), settings.getAddress());
        editor.putInt(mContext.getString(R.string.settings_key_port), settings.getPort());
        editor.putInt(mContext.getString(R.string.settings_key_default_index), index);
        editor.apply();
        defaultIndex = index;
    }

    public void setLastUpdated(Date lastChecked) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(mContext.getString(R.string.settings_key_last_update_check), lastChecked.getTime());
        editor.apply();
    }

    public Date getLastUpdated() {
        return new Date(mPreferences.getLong(mContext.getString(R.string.settings_key_last_update_check), 0));
    }

    public ConnectionSettingsChanged produceConnectionSettings() {
        return new ConnectionSettingsChanged(mSettings, defaultIndex);
    }

    public void handleSettingsChange(ChangeSettings event) {
        switch (event.getAction()) {
            case DELETE:
                mSettings.remove(event.getIndex());
                if (event.getIndex() == defaultIndex && mSettings.size() > 0) {
                    updateDefault(0, mSettings.get(0));
                    new MessageEvent(UserInputEventType.SETTINGS_CHANGED);
                } else {
                    updateDefault(0, new ConnectionSettings());
                }
                storeSettings();
                break;
            case EDIT:
                break;
            case DEFAULT:
                ConnectionSettings settings = mSettings.get(event.getIndex());
                updateDefault(event.getIndex(), settings);
                new ConnectionSettingsChanged(mSettings, event.getIndex());
                new MessageEvent(UserInputEventType.SETTINGS_CHANGED);
                break;
        }
    }

    public SearchDefaultAction produceAction() {
        return new SearchDefaultAction(mPreferences.getString(
                mContext.getString(R.string.settings_search_default_key),
                mContext.getString(R.string.search_click_default_value)));
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
    
    private void checkForFirstRunAfterUpdate() {
        long lastVersionCode = mPreferences.getLong(mContext.
                getString(R.string.settings_key_last_version_run), 0);
        long currentVersion = BuildConfig.VERSION_CODE;

        if (lastVersionCode < currentVersion) {
            isFirstRun = true;

            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putLong(mContext.getString(R.string.settings_key_last_version_run), currentVersion);
            editor.apply();

            if (BuildConfig.DEBUG) {
                Ln.d("update or fresh install");
            }
        }
    }
}
