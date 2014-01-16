package com.kelsos.mbrc.ui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreCursorAdapter;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Genre;
import com.kelsos.mbrc.data.GenreEntry;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.net.Protocol;
import com.kelsos.mbrc.ui.activities.Profile;
import com.kelsos.mbrc.ui.base.BaseListFragment;

public class BrowseGenreFragment extends BaseListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
    private static final int GROUP_ID = 11;
    private static final int URL_LOADER = 1;
    private GenreCursorAdapter mAdapter;
    private String mFilter;
    private SearchView mSearchView;
    private MenuItem mSearchItem;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        menu.add(GROUP_ID, BrowseMenuItems.GET_SUB, 0, R.string.search_context_get_artists);
        menu.add(GROUP_ID, BrowseMenuItems.PLAYLIST, 0, getString(R.string.search_context_playlist));
    }

    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        if (item.getGroupId() == GROUP_ID) {
            AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Object line = mAdapter.getItem(mi.position);
            final String qContext = Protocol.LIBRARY_QUEUE_GENRE;
            final String gSub = Protocol.LIBRARY_GENRE_ARTISTS;
            String query = ((GenreEntry) line).getName();

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
        if (mFilter != null) {
            baseUri = Uri.withAppendedPath(Genre.CONTENT_FILTER_URI, Uri.encode(mFilter));
        } else {
            baseUri = Genre.CONTENT_URI;
        }

        return new CursorLoader(getActivity(), baseUri, Genre.FIELDS, null, null, null);
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter = new GenreCursorAdapter(getActivity(), data, 0);
        setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override public boolean onQueryTextSubmit(String query) {
        mFilter = !TextUtils.isEmpty(query) ? query : null;
        getLoaderManager().restartLoader(URL_LOADER, null, this);
        mSearchView.setIconified(true);
        mSearchItem.collapseActionView();
        return false;
    }

    @Override public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchView = new SearchView(((ActionBarActivity) getActivity()).getSupportActionBar().getThemedContext());
        mSearchView.setQueryHint("Search for Genre");
        mSearchView.setIconifiedByDefault(true);
        mSearchItem = menu.findItem(R.id.now_playing_search_item);
        mSearchItem.setActionView(mSearchView);
        mSearchView.setOnQueryTextListener(this);
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Genre genre = new Genre((Cursor) mAdapter.getItem(position));
        Intent intent = new Intent(getActivity(), Profile.class);
        intent.putExtra("name", genre.getGenreName());
        intent.putExtra("id", genre.getId());
        intent.putExtra("type", "genre");
        startActivity(intent);
    }
}
