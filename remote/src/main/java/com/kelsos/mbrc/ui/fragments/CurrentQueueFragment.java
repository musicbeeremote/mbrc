package com.kelsos.mbrc.ui.fragments;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.*;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.CurrentQueueAdapter;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.kelsos.mbrc.data.Sync;
import com.kelsos.mbrc.data.db.LibraryProvider;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboListFragment;


public class CurrentQueueFragment extends RoboListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int URL_LOADER = 0;
    private CurrentQueueAdapter mQueueAdapter;
    private SearchView mSearchView;
    private String mCurFilter;

    @Inject
    private Sync sync;
    private MenuItem mSearchItem;

    @Override
    public void onStart() {
        super.onStart();
        sync.startCurrentQueueSyncing();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_now_playing, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setIconifiedByDefault(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            mSearchView.setIconified(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(URL_LOADER, null, this);
        mQueueAdapter = new CurrentQueueAdapter(getActivity(), null, 0);
        setListAdapter(mQueueAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ui_fragment_nowplaying, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    private int calculateNewIndex(int from, int to, int index) {
        int dist = Math.abs(from - to);
        int rIndex = index;
        if (dist == 1 && index == from
                || dist > 1 && from > to && index == from
                || dist > 1 && from < to && index == from) {
            rIndex = to;
        } else if (dist == 1 && index == to) {
            rIndex = from;
        } else if (dist > 1 && from > to && index == to
                || from > index && to < index) {
            rIndex += 1;
        } else if (dist > 1 && from < to && index == to
                || from < index && to > index) {
            rIndex -= 1;
        }
        return rIndex;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                QueueTrackDao.Properties.Id.columnName,
                QueueTrackDao.Properties.Artist.columnName,
                QueueTrackDao.Properties.Title.columnName
        };

        Uri baseUri;

        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(LibraryProvider.CONTENT_FILTER_URI, Uri.encode(mCurFilter));
        } else {
            baseUri = LibraryProvider.CONTENT_URI;
        }

        return new CursorLoader(getActivity(), baseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mQueueAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mQueueAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mCurFilter = !TextUtils.isEmpty(s) ? s.trim() : null;
        getLoaderManager().restartLoader(0, null, this);
        MenuItemCompat.collapseActionView(mSearchItem);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
