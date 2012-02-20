package kelsos.mbremote;

import android.app.ListActivity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ListView;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Network.ReplyHandler;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Network.ProtocolHandler.PlayerAction;

import java.util.Timer;
import java.util.TimerTask;


public class PlaylistActivity extends ListActivity {
    private ConnectivityHandler mBoundService;
    private boolean mIsBound;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((ConnectivityHandler.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    void doBindService() {
        bindService(new Intent(PlaylistActivity.this, ConnectivityHandler.class),
                mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private void updateListData() {
        PlaylistArrayAdapter adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, ReplyHandler.getInstance().getNowPlayingList());
        setListAdapter(adapter);
        //ReplyHandler.getInstance().clearNowPlayingList();
    }

    private class RequestPlaylistTask extends TimerTask {
        @Override
        public void run() {
            mBoundService.requestHandler().requestAction(PlayerAction.Playlist);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(PlaylistActivity.this,
                ConnectivityHandler.class));
        doBindService();
        IntentFilter plFilter = new IntentFilter();
        plFilter.addAction(Intents.PLAYLIST_DATA);
        registerReceiver(mReceiver, plFilter);
        Timer reqTimer = new Timer();
        RequestPlaylistTask rpt = new RequestPlaylistTask();
        reqTimer.schedule(rpt, 1000);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MusicTrack track = (MusicTrack) getListView().getItemAtPosition(position);
        mBoundService.requestHandler().requestAction(PlayerAction.PlayNow, track.getTitle());
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
