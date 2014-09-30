package com.kelsos.mbrc.ui.fragments.browse;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackCursorAdapter;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.data.dbdata.Track;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;

public class BrowseTrackFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, PlaylistDialogFragment.onPlaylistSelectedListener {
    private static final int GROUP_ID = 14;
    private static final int URL_LOADER = 0x53;
    private TrackCursorAdapter mAdapter;
    private Track track;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {

        }

        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Track.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[]{Track.TITLE, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new TrackCursorAdapter(getActivity(), data, 0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onPlaylistSelected(String hash) {

    }

    @Override
    public void onNewPlaylistSelected() {

    }

}
