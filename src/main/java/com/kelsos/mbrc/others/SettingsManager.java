package com.kelsos.mbrc.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.ConnectionSettings;
import com.kelsos.mbrc.events.ui.ChangeSettings;
import com.kelsos.mbrc.events.ui.ConnectionSettingsChanged;
import com.kelsos.mbrc.events.ui.NoSettingsAvailable;
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
	private SharedPreferences preferences;
	private Context context;
	private MainThreadBusWrapper bus;
    private ArrayList<ConnectionSettings> mSettings;
    private ObjectMapper mMapper;

    @Inject public SettingsManager(Context context, SharedPreferences preferences, MainThreadBusWrapper bus, ObjectMapper mapper) {
        this.preferences = preferences;
        this.context = context;
        this.bus = bus;
        this.mMapper = mapper;
        bus.register(this);

        String sVal = preferences.getString(context.getString(R.string.settings_array), null);

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
        Log.d("Settings", mSettings.toString());
	}

	public SocketAddress getSocketAddress() {
		String server_hostname = preferences.getString(context.getString(R.string.settings_key_hostname), null);
		String server_port_string = preferences.getString(context.getString(R.string.settings_key_port), null);
		if (nullOrEmpty(server_hostname) || nullOrEmpty(server_port_string)) {
            bus.post(new NoSettingsAvailable());
			return null;
		}
		int server_port = Integer.parseInt(server_port_string);

		return new InetSocketAddress(server_hostname, server_port);
	}

	public boolean isVolumeReducedOnRinging() {
		return preferences.getBoolean(context.getString(R.string.settings_key_reduce_volume), false);
	}

	private static boolean nullOrEmpty(String string) {
		return string == null || string.equals("");
	}

    private void storeSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString(context.getString(R.string.settings_array), mMapper.writeValueAsString(mSettings));
            editor.commit();
            bus.post(new ConnectionSettingsChanged(mSettings));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe public void handleConnectionSettings(ConnectionSettings settings) {
        if (!mSettings.contains(settings)) {
            mSettings.add(settings);
            storeSettings();
        }
        //todo: add some kind of message that the settings already exist on else;
    }

    @Produce public ConnectionSettingsChanged produceConnectionSettings() {
        return new ConnectionSettingsChanged(mSettings);
    }

    @Subscribe public void handleSettingsChange(ChangeSettings event) {
        switch (event.getAction()) {
            case DELETE:
                mSettings.remove(event.getIndex());
                storeSettings();
                break;
            case EDIT:
                break;
            case DEFAULT:
                ConnectionSettings settings = mSettings.get(event.getIndex());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(context.getString(R.string.settings_key_hostname), settings.getAddress());
                editor.putString(context.getString(R.string.settings_key_port), Integer.toString(settings.getPort()));
                editor.commit();
                //bus.post(new ConnectionSettingsChanged(mSettings));
                break;
        }
    }
}
