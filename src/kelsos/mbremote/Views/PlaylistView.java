package kelsos.mbremote.Views;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.google.inject.Inject;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Events.PlaylistViewAction;
import kelsos.mbremote.Events.PlaylistViewEvent;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.XmlEncoder;
import kelsos.mbremote.R;
import roboguice.event.EventManager;

import java.util.ArrayList;

public class PlaylistView extends ListActivity {

    @Inject protected EventManager eventManager;
    private DelayTimer delayTimer;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList) {
        PlaylistArrayAdapter adapter;
        adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
        setListAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delayTimer = new DelayTimer(1200);
        delayTimer.setTimerFinishEventListener(timerFinishEvent);
        delayTimer.start();
        //Controller.getInstance().onActivityStart(this);
    }

    DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {

        public void onTimerFinish() {
            eventManager.fire(new PlaylistViewEvent(this, PlaylistViewAction.GetPlaylist));
        }
    };

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String track = ((MusicTrack) getListView().getItemAtPosition(position)).getTitle();
        eventManager.fire(new PlaylistViewEvent(this, PlaylistViewAction.PlaySpecifiedTrack, XmlEncoder.encode(track)));

    }
}


