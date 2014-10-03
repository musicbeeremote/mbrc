package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistTrackCursorAdapter;
import com.kelsos.mbrc.ui.base.BaseActivity;
import com.kelsos.mbrc.util.MainThreadBusWrapper;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import roboguice.inject.InjectView;

public class PlaylistActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URL_LOADER = 0x721ae;
    @Inject private MainThreadBusWrapper bus;
    @InjectView(android.R.id.list) private ListView mList;
    private PlaylistTrackCursorAdapter mAdapter;
    private String mTitle;
    private String mHash;
    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public DragSortController buildController(DragSortListView dslv) {
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
        mHash = intent.getStringExtra("hash");
        mDslv = (DragSortListView) mList;
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter = new PlaylistTrackCursorAdapter(this,cursor,0);
        mList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
