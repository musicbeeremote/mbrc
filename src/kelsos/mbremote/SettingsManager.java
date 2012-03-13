package kelsos.mbremote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SettingsManager {

    private Context appContext;
    private SharedPreferences sharedPreferences;

    public SettingsManager(Context appContext) {
        this.appContext = appContext;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(appContext);
    }

    public SocketAddress getSocketAddress() {
        String server_hostname = sharedPreferences.getString(
                appContext.getString(R.string.settings_server_hostname), null);
        String server_port_string = sharedPreferences.getString(
                appContext.getString(R.string.settings_server_port), null);
        if (checkForNullOrEmpty(server_hostname)
                || checkForNullOrEmpty(server_port_string)) {
            Toast.makeText(appContext,
                    R.string.network_manager_check_hostname_or_port,
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        int server_port = Integer.parseInt(server_port_string);

        return new InetSocketAddress(server_hostname, server_port);
    }

    public boolean isVolumeReducedOnRinging() {
        return sharedPreferences.getBoolean(appContext.getString(
                R.string.settings_reduce_volume_on_ring), false);
    }

    private static boolean checkForNullOrEmpty(String string) {
        if (string == null || string == "")
            return true;
        return false;

    }
}
