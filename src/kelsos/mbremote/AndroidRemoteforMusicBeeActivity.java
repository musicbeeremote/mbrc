package kelsos.mbremote;

import kelsos.mbremote.Network.AnswerHandler;
import kelsos.mbremote.Network.NetworkManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AndroidRemoteforMusicBeeActivity extends Activity {
	/** Called when the activity is first created. */
	private NetworkManager mBoundService;
	private boolean mIsBound;
	private boolean userChangingVolume;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(AndroidRemoteforMusicBeeActivity.this,
				NetworkManager.class));
		doBindService();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AnswerHandler.VOLUME_DATA);
		registerReceiver(mReceiver, filter);
		// Buttons and listeners
		ImageButton playButton = (ImageButton) findViewById(R.id.playPauseButton);
		playButton.setOnClickListener(playButtonListener);
		ImageButton previousButton = (ImageButton) findViewById(R.id.previousButton);
		previousButton.setOnClickListener(previousButtonListener);
		ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(nextButtonListener);
		SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
		volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
		ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(stopButtonListener);
		ImageButton muteButton = (ImageButton) findViewById(R.id.muteButton);
		muteButton.setOnClickListener(muteButtonListener);
		ImageButton scrobbleButton = (ImageButton) findViewById(R.id.scrobbleButton);
		scrobbleButton.setOnClickListener(scrobbleButtonListerner);
		ImageButton shuffleButton = (ImageButton) findViewById(R.id.shuffleButton);
		shuffleButton.setOnClickListener(shuffleButtonListener);
		ImageButton repeatButton = (ImageButton) findViewById(R.id.repeatButton);
		repeatButton.setOnClickListener(repeatButtonListener);

		userChangingVolume=false;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(AnswerHandler.VOLUME_DATA))
			{
				SeekBar volumeSlider = (SeekBar) findViewById(R.id.volumeSlider);
				if(!userChangingVolume)
					volumeSlider.setProgress(intent.getExtras().getInt("data"));
			}
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBoundService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBoundService = ((NetworkManager.LocalBinder) service).getService();
		}
	};

	void doBindService() {
		bindService(new Intent(AndroidRemoteforMusicBeeActivity.this,
				NetworkManager.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
		unregisterReceiver(mReceiver);
	}

	private OnClickListener playButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<playPause/>\r\n");
		}
	};
	private OnClickListener previousButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<previous/>\r\n");
		}
	};

	private OnClickListener nextButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<next/>\r\n");
		}
	};

	private OnClickListener stopButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<stopPlayback/>\r\n");
		}
	};

	private OnClickListener muteButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<mute>toggle</mute>\r\n");

		}
	};

	private OnClickListener scrobbleButtonListerner = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<scrobbler>toggle</scrobbler>\r\n");

		}
	};

	private OnClickListener shuffleButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<shuffle>toggle</shuffle>\r\n");
		}
	};

	private OnClickListener repeatButtonListener = new OnClickListener() {

		public void onClick(View v) {
			mBoundService.sendData("<repeat>toggle</repeat>\r\n");
		}
	};

	private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {

		public void onStopTrackingTouch(SeekBar seekBar) {
			userChangingVolume=false;

		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			userChangingVolume=true;

		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(fromUser)
				mBoundService.sendData("<volume>" + seekBar.getProgress() + "</volume>");
		}
	};
}