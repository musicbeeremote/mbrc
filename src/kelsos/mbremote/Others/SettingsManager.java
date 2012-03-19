package kelsos.mbremote.Others;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import kelsos.mbremote.Messaging.AppNotificationManager;
import kelsos.mbremote.R;

public class SettingsManager {

    private static SettingsManager _instance = new SettingsManager();

	private Context context;
	private SharedPreferences sharedPreferences;

	private SettingsManager() {
	}

    public static SettingsManager getInstance()
    {
        return _instance;
    }
    
    public void setContext(Context context)
    {
        if(this.context==null)
            this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

	public SocketAddress getSocketAddress(){
		String server_hostname = sharedPreferences.getString(context.getString(R.string.settings_server_hostname), null);
		String server_port_string = sharedPreferences.getString(context.getString(R.string.settings_server_port), null);
		if (notNullOrEmpty(server_hostname) || notNullOrEmpty(server_port_string)) {
            AppNotificationManager.getInstance().showToastMessage(R.string.network_manager_check_hostname_or_port);
			return null;
		}
		int server_port = Integer.parseInt(server_port_string);

		return new InetSocketAddress(server_hostname, server_port);
	}

	public boolean isVolumeReducedOnRinging(){
        return sharedPreferences.getBoolean(context.getString(R.string.settings_reduce_volume_on_ring), false);
	}

	private static boolean notNullOrEmpty(String string) {
		if (string == null || string == "")
			return true;
		return false;

	}
}
