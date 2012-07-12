package kelsos.mbremote.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.google.inject.Inject;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Enumerations.PlaylistViewAction;
import kelsos.mbremote.Events.PlaylistViewEvent;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.XmlEncoder;
import kelsos.mbremote.R;
import roboguice.activity.RoboListActivity;
import roboguice.event.EventManager;

import java.util.ArrayList;

public class PlaylistView extends RoboListActivity {

    @Inject protected EventManager eventManager;
    @Inject private Controller controller;

    private DelayTimer delayTimer;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList) {
        PlaylistArrayAdapter adapter;
        adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
        setListAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller.onActivityStart(this);
        delayTimer = new DelayTimer(1200, timerFinishEvent);
        delayTimer.start();
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


