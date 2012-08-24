package kelsos.mbremote.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Data.PlaylistArrayAdapter;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.R;
import kelsos.mbremote.controller.RunningActivityAccessor;
import kelsos.mbremote.enums.UserInputEventType;

import java.util.ArrayList;

public class PlaylistView extends RoboSherlockListActivity
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private Bus bus;

    public void updateListData(ArrayList<MusicTrack> nowPlayingList) {
        PlaylistArrayAdapter adapter;
        adapter = new PlaylistArrayAdapter(this, R.layout.playlistview_item, nowPlayingList);
        setListAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.nowplayinglist);
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.Playlist));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.string_value_now_playing);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String track = ((MusicTrack) getListView().getItemAtPosition(position)).getTitle();
		bus.post(new UserActionEvent(UserInputEventType.PlaySpecifiedTrack,track));

    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			    finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}


