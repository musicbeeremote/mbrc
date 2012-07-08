package kelsos.mbremote.Others;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import kelsos.mbremote.R;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SettingsManager {
    @Inject SharedPreferences preferences;
    @Inject Context context;

	public SettingsManager() {
	}

	public SocketAddress getSocketAddress(){
		String server_hostname = preferences.getString(context.getString(R.string.settings_server_hostname), null);
		String server_port_string = preferences.getString(context.getString(R.string.settings_server_port), null);
		if (notNullOrEmpty(server_hostname) || notNullOrEmpty(server_port_string)) {
            //NotificationService.getInstance().showToastMessage(R.string.network_manager_check_hostname_or_port);
			return null;
		}
		int server_port = Integer.parseInt(server_port_string);

		return new InetSocketAddress(server_hostname, server_port);
        	}

	public boolean isVolumeReducedOnRinging(){
        return preferences.getBoolean(context.getString(R.string.settings_reduce_volume_on_ring), false);
	}

	private static boolean notNullOrEmpty(String string) {
		if (string == null || string == "")
			return true;
		return false;

	}
}
