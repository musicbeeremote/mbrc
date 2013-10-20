package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackEntryAdapter;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;

public class PlaylistActivity extends RoboSherlockActivity{
    @Inject
    private MainThreadBusWrapper bus;
    private TrackEntryAdapter adapter;
    private String mTitle;
    private String mSrc;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("name");
        mSrc = intent.getStringExtra("src");
        setContentView(R.layout.ui_activity_connection_manager);
    }

    @Override public void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
        bus.register(this);
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PlaylistGetFiles, mSrc)));
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    //@Subscribe public void handlePlaylistData() {
    //adapter = new PlaylistAdapter(getActivity(), R.layout.ui_list_dual, );
    //setListAdapter(adapter);
    //adapter.notifyDataSetChanged();
    //}

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
