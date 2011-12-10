package kelsos.mbremote;

import kelsos.mbremote.Network.NetworkManager;
import android.app.Activity;
import android.os.Bundle;

public class AndroidRemoteforMusicBeeActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Thread cThread = new Thread(new NetworkManager());
		cThread.start();
    }
}