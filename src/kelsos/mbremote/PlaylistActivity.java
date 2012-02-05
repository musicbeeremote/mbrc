package kelsos.mbremote;

import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Network.AnswerHandler;
import kelsos.mbremote.Network.NetworkManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


public class PlaylistActivity extends ListActivity {
	private NetworkManager mBoundService;
	private boolean mIsBound;

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((NetworkManager.LocalBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
		}
	};

	void doBindService() {
		bindService(new Intent(PlaylistActivity.this, NetworkManager.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	private void updateListData()
	{
		PlaylistArrayAdapter adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, AnswerHandler.getInstance().getNowPlayingList());
		setListAdapter(adapter);
		//AnswerHandler.getInstance().clearNowPlayingList();
	}
	
	private class RequestPlaylistTask extends TimerTask
	{
		@Override
		public void run() {
			mBoundService.requestPlaylist();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        startService(new Intent(PlaylistActivity.this,
                NetworkManager.class));
		doBindService();
		IntentFilter plFilter = new IntentFilter();
		plFilter.addAction(AnswerHandler.PLAYLIST_DATA);
		registerReceiver(mReceiver, plFilter);
		Timer reqTimer = new Timer();
		RequestPlaylistTask rpt = new RequestPlaylistTask();
		reqTimer.schedule(rpt, 1000);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		MusicTrack track = (MusicTrack) getListView().getItemAtPosition(position);
		Log.d("track",track.getTitle());
		mBoundService.requestPlaySelectedTrackNow(track.getTitle());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
		unregisterReceiver(mReceiver);
	}
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			updateListData();
			
		}
	};
	
	
}
