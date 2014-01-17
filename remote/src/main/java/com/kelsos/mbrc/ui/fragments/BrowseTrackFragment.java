package com.kelsos.mbrc.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.data.*;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseListFragment;

public class BrowseTrackFragment extends BaseListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GROUP_ID = 14;
    private static final int URL_LOADER = 0x53;
    private SimpleCursorAdapter mAdapter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        return inflater.inflate(R.layout.ui_fragment_library_simpl, container, false);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = mAdapter.getItem(mi.position);
            final String qContext = Protocol.LIBRARY_QUEUE_TRACK;
            final String query = ((TrackEntry) line).getSrc();

            UserAction ua = null;
            switch (item.getItemId()) {
                case BrowseMenuItems.QUEUE_NEXT:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_next), query));
                    break;
                case BrowseMenuItems.QUEUE_LAST:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_last), query));
                    break;
                case BrowseMenuItems.PLAY_NOW:
                    ua = new UserAction(qContext, new Queue(getString(R.string.mqueue_now), query));
                    break;
                default:
                    return false;
            }

            //if (ua != null) bus.post(new MessageEvent(ProtocolEventType.USER_ACTION, ua));
            return true;
        } else {
            return false;
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Track.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Track.TITLE, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_dual,
                data,
                new String[] {Track.TITLE, Artist.ARTIST_NAME},
                new int[] {R.id.line_one, R.id.line_two},
                0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) { }
}
