package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.kelsos.mbrc.events.UserActionEvent;
import com.kelsos.mbrc.others.SettingsManager;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;
import com.kelsos.mbrc.enums.UserInputEventType;
import roboguice.receiver.RoboBroadcastReceiver;

public class RemoteBroadcastReceiver extends RoboBroadcastReceiver
{
	private SettingsManager settingsManager;
	private Bus bus;
	private Context context;

	@Inject
	public RemoteBroadcastReceiver(SettingsManager settingsManager, Bus bus, Context context)
	{
		this.settingsManager = settingsManager;
		this.bus = bus;
		this.context = context;
		this.installFilter();
	}

	@Override
	protected void handleReceive(Context context, Intent intent) {
		if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
		{
			Bundle bundle = intent.getExtras();
			if (null == bundle) return;
			String state = bundle.getString(TelephonyManager.EXTRA_STATE);
			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
			{
				if (settingsManager.isVolumeReducedOnRinging())
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REDUCE_VOLUME));

				}
			}
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
			{
				 bus.post(new UserActionEvent(UserInputEventType.USERINPUT_EVENT_REQUEST_INITIALIZE));
			} else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
			{

			}
		}
	}

	/**
	 * Initialized and installs the IntentFilter listening for the SONG_CHANGED
	 * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
	 * Android operating system.
	 */
	private void installFilter()
	{
		IntentFilter _nmFilter = new IntentFilter();
		_nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		_nmFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		context.registerReceiver(this, _nmFilter);
	}




}
