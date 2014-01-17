package com.kelsos.mbrc.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.AlbumCursorAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.*;
import com.kelsos.mbrc.data.dbdata.Album;
import com.kelsos.mbrc.data.dbdata.Artist;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.base.BaseFragment;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

public class BrowseAlbumFragment extends BaseFragment implements LoaderCallbacks<Cursor> {
    private static final int GROUP_ID = 13;
    private static final int URL_LOADER = 2;
    private AlbumCursorAdapter mAdapter;
    private GridView mGridView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getLoaderManager().initLoader(URL_LOADER, null, this);
        View view = inflater.inflate(R.layout.ui_library_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.mbrc_grid_view);

        return view;
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.search_context_header);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_NEXT, 0, R.string.search_context_queue_next);
        menu.add(GROUP_ID, BrowseMenuItems.QUEUE_LAST, 0, R.string.search_context_queue_last);
        menu.add(GROUP_ID, BrowseMenuItems.PLAY_NOW, 0, R.string.search_context_play_now);
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_tracks);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = mAdapter.getItem(mi.position);
            final String qContext = Protocol.LIBRARY_QUEUE_ALBUM;
            final String gSub = Protocol.LIBRARY_ALBUM_TRACKS;
            String query = ((AlbumEntry) line).getAlbum();

            UserAction ua;
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
                default:
                    return false;
            }

            getBus().post(new MessageEvent(ProtocolEventType.USER_ACTION, ua));
            return true;
        } else {
            return false;
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        baseUri = Album.getContentUri();
        return new CursorLoader(getActivity(), baseUri,
                new String[] {Album.ALBUM_NAME, Artist.ARTIST_NAME}, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new AlbumCursorAdapter(getActivity(), data, 0);
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) { }
}
