package kelsos.mbremote.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import kelsos.mbremote.Events.ProtocolDataEvent;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.Others.SettingsManager;
import kelsos.mbremote.enums.ProtocolHandlerEventType;
import kelsos.mbremote.enums.UserInputEventType;
import roboguice.receiver.RoboBroadcastReceiver;

public class RemoteBroadcastReceiver extends RoboBroadcastReceiver
{
	private SettingsManager settingsManager;
	private Bus bus;

	@Inject
	public RemoteBroadcastReceiver(SettingsManager settingsManager, Bus bus)
	{
		this.settingsManager = settingsManager;
		this.bus = bus;
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
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REDUCE_VOLUME,""));

				}
			}
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
		{
			NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
			{
				 bus.post(new UserActionEvent(UserInputEventType.Initialize));
			} else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
			{

			}
		}
	}


}
