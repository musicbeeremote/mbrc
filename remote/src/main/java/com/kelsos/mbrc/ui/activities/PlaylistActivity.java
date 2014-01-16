package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistTrackAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.ui.PlaylistTracksAvailable;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.squareup.otto.Subscribe;
import roboguice.inject.InjectView;

public class PlaylistActivity extends BaseActivity {
    @Inject private MainThreadBusWrapper bus;
    @InjectView(android.R.id.list) ListView mList;
    private PlaylistTrackAdapter adapter;
    private String mTitle;
    private String mSrc;

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        // dragStartMode = onDown
        // removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setFlingHandleId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_fragment_nowplaying);
        Intent intent = getIntent();
        mTitle = intent.getStringExtra("name");
        mSrc = intent.getStringExtra("src");
        mDslv = (DragSortListView) mList;
        //mDslv.setDropListener(onDrop);
        //mDslv.setRemoveListener(onRemove);
        registerForContextMenu(mList);
        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);
    }

    @Override public void onStart() {
        super.onStart();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mTitle);
        bus.register(this);
        bus.post(new MessageEvent(ProtocolEventType.UserAction, new UserAction(Protocol.PLAYLIST_GET_FILES, mSrc)));
    }

    @Override public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe public void handlePlaylistData(PlaylistTracksAvailable event) {
        adapter = new PlaylistTrackAdapter(this, R.layout.ui_list_track_item, event.getPlaylistTracks());
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

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
