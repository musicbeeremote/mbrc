package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.ProtocolEvent;
import com.kelsos.mbrc.events.UserInputEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.messaging.NotificationService;
import com.kelsos.mbrc.others.Protocol;
import com.kelsos.mbrc.others.SettingsManager;
import com.squareup.otto.Bus;
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
					bus.post(new MessageEvent(ProtocolEvent.ReduceVolume));
				}
			}
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
			{
				 bus.post(new MessageEvent(UserInputEvent.StartConnection));
			} else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
			{

			}
		} else if (intent.getAction().equals(NotificationService.NOTIFICATION_PLAY_PRESSED)) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerPlayPause, true)));
		} else if (intent.getAction().equals(NotificationService.NOTIFICATION_NEXT_PRESSED)) {
            bus.post(new MessageEvent(ProtocolEvent.UserAction, new UserAction(Protocol.PlayerNext, true)));
		} else if(intent.getAction().equals(NotificationService.NOTIFICATION_CLOSE_PRESSED)) {
			bus.post(new MessageEvent(UserInputEvent.CancelNotification));
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
		_nmFilter.addAction(NotificationService.NOTIFICATION_PLAY_PRESSED);
		_nmFilter.addAction(NotificationService.NOTIFICATION_NEXT_PRESSED);
		_nmFilter.addAction(NotificationService.NOTIFICATION_CLOSE_PRESSED);
		context.registerReceiver(this, _nmFilter);
	}




}
