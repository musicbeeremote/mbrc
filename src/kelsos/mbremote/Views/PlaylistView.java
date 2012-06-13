package kelsos.mbremote.Views;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Events.PlaylistViewAction;
import kelsos.mbremote.Events.PlaylistViewEvent;
import kelsos.mbremote.Events.PlaylistViewEventSource;
import kelsos.mbremote.Events.PlaylistViewListener;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.XmlEncoder;
import kelsos.mbremote.R;

import java.util.ArrayList;

public class PlaylistView extends ListActivity {

    private DelayTimer delayTimer;
    private PlaylistViewEventSource _eventSource;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList) {
        PlaylistArrayAdapter adapter;
        adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
        setListAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _eventSource = new PlaylistViewEventSource();
        delayTimer = new DelayTimer(1200);
        delayTimer.setTimerFinishEventListener(timerFinishEvent);
        delayTimer.start();
        Controller.getInstance().onActivityStart(this);
    }

    DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {

        public void onTimerFinish() {
            _eventSource.dispatchEvent(new PlaylistViewEvent(this, PlaylistViewAction.GetPlaylist));
        }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String track = ((MusicTrack) getListView().getItemAtPosition(position)).getTitle();
        _eventSource.dispatchEvent(new PlaylistViewEvent(this, PlaylistViewAction.PlaySpecifiedTrack, XmlEncoder.encode(track)));

    }

    public void addEventListener(PlaylistViewListener listener) {
        _eventSource.addEventListener(listener);
    }

    public void removeEventListener(PlaylistViewListener listener) {
        _eventSource.removeEventListener(listener);
    }
}


