package kelsos.mbremote;

import kelsos.mbremote.Network.NetworkManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class AndroidRemoteforMusicBeeActivity extends Activity {
	/** Called when the activity is first created. */
	private NetworkManager mBoundService;
	private boolean mIsBound;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		 startService(new Intent(AndroidRemoteforMusicBeeActivity.this,NetworkManager.class));
         doBindService(); 
    }
	private ServiceConnection mConnection = new ServiceConnection() {
		
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBoundService = null;
		}
		
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBoundService = ((NetworkManager.LocalBinder)service).getService();
		}
	};
	
	void doBindService(){
		bindService(new Intent(AndroidRemoteforMusicBeeActivity.this,NetworkManager.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}
	
	void doUnbindService()
	{
		if(mIsBound)
		{
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		doUnbindService();
	}

}