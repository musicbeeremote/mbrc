package com.kelsos.mbrc.others;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.messaging.NotificationService;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class SettingsManager
{
	@Inject
	SharedPreferences preferences;
	@Inject
	Context context;
	@Inject
	NotificationService notificationService;

	public SettingsManager()
	{
	}

	public SocketAddress getSocketAddress()
	{
		String server_hostname = preferences.getString(context.getString(R.string.settings_key_hostname), null);
		String server_port_string = preferences.getString(context.getString(R.string.settings_key_port), null);
		if (notNullOrEmpty(server_hostname) || notNullOrEmpty(server_port_string))
		{
			notificationService.showToastMessage(R.string.notification_check_network_settings);
			return null;
		}
		int server_port = Integer.parseInt(server_port_string);

		return new InetSocketAddress(server_hostname, server_port);
	}

	public boolean isVolumeReducedOnRinging()
	{
		return preferences.getBoolean(context.getString(R.string.settings_key_reduce_volume), false);
	}

	private static boolean notNullOrEmpty(String string)
	{
		return string == null || string.equals("");
	}
}
