package com.kelsos.mbrc.others;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.NoSettingsAvailable;
import com.kelsos.mbrc.events.ui.NotifyUser;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

@Singleton
public class SettingsManager {
	private SharedPreferences mPreferences;
	private Context mContext;
	private MainThreadBusWrapper bus;
    private ArrayList<ConnectionSettings> mSettings;
    private ObjectMapper mMapper;
    private int defaultIndex;

    @Inject public SettingsManager(Context context, SharedPreferences preferences, MainThreadBusWrapper bus, ObjectMapper mapper) {
        this.mPreferences = preferences;
        this.mContext = context;
        this.bus = bus;
        this.mMapper = mapper;
        bus.register(this);

        String sVal = preferences.getString(context.getString(R.string.settings_key_array), null);
        mSettings = new ArrayList<ConnectionSettings>();

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
                e.printStackTrace();
            }
        }
        defaultIndex = mPreferences.getInt(mContext.getString(R.string.settings_key_default_index), 0);
	}

	public SocketAddress getSocketAddress() {
		String serverAddress = mPreferences.getString(mContext.getString(R.string.settings_key_hostname), null);
		int serverPort = mPreferences.getInt(mContext.getString(R.string.settings_key_port), 0);
		if (nullOrEmpty(serverAddress) || serverPort == 0) {
            bus.post(new NoSettingsAvailable());
			return null;
		}

		return new InetSocketAddress(serverAddress, serverPort);
	}

	public boolean isVolumeReducedOnRinging() {
		return mPreferences.getBoolean(mContext.getString(R.string.settings_key_reduce_volume), false);
	}

	private static boolean nullOrEmpty(String string) {
		return string == null || string.equals("");
	}

    private void storeSettings() {
        SharedPreferences.Editor editor = mPreferences.edit();
        try {
            editor.putString(mContext.getString(R.string.settings_key_array), mMapper.writeValueAsString(mSettings));
            editor.commit();
            bus.post(new ConnectionSettingsChanged(mSettings, 0));
        } catch (IOException e) {
        }
    }

    @Subscribe public void handleConnectionSettings(ConnectionSettings settings) {
        if (settings.getIndex() < 0) {
            if (!mSettings.contains(settings)) {
                if (mSettings.size() == 0) {
                    updateDefault(0, settings);
                    bus.post(new MessageEvent(UserInputEvent.SettingsChanged));
                }
                mSettings.add(settings);
                storeSettings();
            } else {
                bus.post(new NotifyUser(R.string.notification_settings_stored));
            }
        } else {
            mSettings.set(settings.getIndex(), settings);
            if (settings.getIndex() == defaultIndex) {
                bus.post(new MessageEvent(UserInputEvent.SettingsChanged));
            }
            storeSettings();
        }
    }

    private void updateDefault(int index, ConnectionSettings settings) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(mContext.getString(R.string.settings_key_hostname), settings.getAddress());
        editor.putInt(mContext.getString(R.string.settings_key_port), settings.getPort());
        editor.putInt(mContext.getString(R.string.settings_key_default_index), index);
        editor.commit();
        defaultIndex = index;
    }

    @Produce public ConnectionSettingsChanged produceConnectionSettings() {
        return new ConnectionSettingsChanged(mSettings, defaultIndex);
    }

    @Subscribe public void handleSettingsChange(ChangeSettings event) {
        switch (event.getAction()) {
            case DELETE:
                mSettings.remove(event.getIndex());
                if (event.getIndex() == defaultIndex && mSettings.size()>0) {
                    updateDefault(0, mSettings.get(0));
                    bus.post(new MessageEvent(UserInputEvent.SettingsChanged));
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
                bus.post(new ConnectionSettingsChanged(mSettings, event.getIndex()));
                bus.post(new MessageEvent(UserInputEvent.SettingsChanged));
                break;
        }
    }
}
