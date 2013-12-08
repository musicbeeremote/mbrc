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
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Artist;
import com.kelsos.mbrc.data.ArtistEntry;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.general.SearchDefaultAction;
import com.kelsos.mbrc.events.ui.ArtistSearchResults;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseListFragment;
import com.squareup.otto.Subscribe;

public class BrowseArtistFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int GROUP_ID = 12;
    private static final int URL_LOADER = 0x12;
    private SimpleCursorAdapter mAdapter;
    private String mDefault;

    @Subscribe public void handleSearchDefaultAction(SearchDefaultAction action) {
        mDefault = action.getAction();
    }

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
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_albums);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = mAdapter.getItem(mi.position);
            final String qContext = Protocol.LibraryQueueArtist;
            final String gSub = Protocol.LibraryArtistAlbums;
            String query = ((ArtistEntry) line).getArtist();

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
                case BrowseMenuItems.GET_SUB:
                    ua = new UserAction(gSub, query);
                    break;
            }

            if (ua != null) getBus().post(new MessageEvent(ProtocolEventType.UserAction, ua));
            return true;
        } else {
            return false;
        }
    }

    @Subscribe public void handleArtistSearchResults(ArtistSearchResults results) {
        //adapter = new ArtistEntryAdapter(getActivity(), R.layout.ui_list_single, results.getList());
        //setListAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Artist.getContentUri();
        return new CursorLoader(getActivity(),baseUri,
                new String[] {Artist.ARTIST_NAME }, null,null,null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.ui_list_single,
                data,
                new String[] { Artist.ARTIST_NAME },
                new int[] { R.id.line_one },
                0);
        this.setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }
}
