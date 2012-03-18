package kelsos.mbremote;

import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Messaging.ClickSource;
import kelsos.mbremote.Messaging.Communicator;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Network.ReplyHandler;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import kelsos.mbremote.Others.Const;

public class PlaylistActivity extends ListActivity {

	private void updateListData() {
		PlaylistArrayAdapter adapter = new PlaylistArrayAdapter(this,
				R.layout.playlistview_item, ReplyHandler.getInstance()
						.getNowPlayingList());
		setListAdapter(adapter);
		// ReplyHandler.getInstance().clearNowPlayingList();
	}

	private class RequestPlaylistTask extends TimerTask {
		@Override
		public void run() {
			Communicator.getInstance().onActivityButtonClicked(ClickSource.Playlist);
			Log.d("PlayList", "Request send");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(PlaylistActivity.this, ConnectivityHandler.class));
		IntentFilter plFilter = new IntentFilter();
		plFilter.addAction(Const.PLAYLIST_DATA);
		registerReceiver(mReceiver, plFilter);
		Timer reqTimer = new Timer();
		RequestPlaylistTask rpt = new RequestPlaylistTask();
		reqTimer.schedule(rpt, 1000);
		Log.d("Playlist", "oncreate");

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		MusicTrack track = (MusicTrack) getListView().getItemAtPosition(position);
		Communicator.getInstance().onPlayNowRequest(track.getTitle());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateListData();

		}
	};
}
