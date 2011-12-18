package kelsos.mbremote;

import kelsos.mbremote.Network.NetworkManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		 startService(new Intent(AndroidRemoteforMusicBeeActivity.this,NetworkManager.class));
         doBindService();
         ImageButton playButton = (ImageButton)findViewById(R.id.playPauseButton);
         playButton.setOnClickListener(playButtonListener);
         ImageButton previousButton = (ImageButton)findViewById(R.id.previousButton);
         previousButton.setOnClickListener(previousButtonListener);
         ImageButton nextButton = (ImageButton)findViewById(R.id.nextButton);
         nextButton.setOnClickListener(nextButtonListener);
         SeekBar volumeSlider = (SeekBar)findViewById(R.id.volumeSlider);
         volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
         
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
	
	private OnClickListener playButtonListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBoundService.sendData("<playpause/>\r\n");
		}
	};
	private OnClickListener previousButtonListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBoundService.sendData("<previous/>\r\n");
		}
	};
	
	private OnClickListener nextButtonListener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mBoundService.sendData("<next/>\r\n");
		}
	};
	private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {
		
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			mBoundService.sendData("<volume>"+seekBar.getProgress()+"</volume>");
		}
	};
}