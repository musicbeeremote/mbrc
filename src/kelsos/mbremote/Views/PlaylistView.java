package kelsos.mbremote.Views;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.DelayTimer;

public class PlaylistView extends ListActivity {

    private DelayTimer delayTimer;

	private void updateListData() {
//		PlaylistArrayAdapter adapter = new PlaylistArrayAdapter(this,
//				R.layout.playlistview_item, ReplyHandler.getInstance()
//						.getNowPlayingList());
//		setListAdapter(adapter);
		// ReplyHandler.getInstance().clearNowPlayingList();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter plFilter = new IntentFilter();
		plFilter.addAction(Const.PLAYLIST_DATA);
		registerReceiver(mReceiver, plFilter);
        delayTimer = new DelayTimer(1200);
        delayTimer.setTimerFinishEventListener(timerFinishEvent);
        delayTimer.start();

	}

    DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {

        public void onTimerFinish() {
//                    Communicator.getInstance().activityButtonClicked(UserAction.Playlist);
        }
    };

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		MusicTrack track = (MusicTrack) getListView().getItemAtPosition(position);
//		Communicator.getInstance().playNowRequest(track.getTitle());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
//        ReplyHandler.getInstance().clearNowPlayingList();
	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateListData();

		}
	};
}
