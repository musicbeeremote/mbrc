package com.kelsos.mbrc.ui.fragments.queue;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.CurrentQueueAdapter;
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.helpers.QueueTrackHelper;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.QueueResultActivity;
import com.kelsos.mbrc.util.Logger;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboListFragment;
import roboguice.util.Ln;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CurrentQueueFragment extends RoboListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int URL_LOADER = 0;
    private CurrentQueueAdapter mQueueAdapter;
    private SearchView mSearchView;

    @Inject
    private SyncManager syncManager;

    @Inject
    private RemoteApi api;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_now_playing, menu);
        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(this);

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
        final Cursor cursor = (Cursor) mQueueAdapter.getItem(position);
        final QueueTrack track = QueueTrackHelper.fromCursor(cursor);

        AndroidObservable.bindFragment(this, api.nowPlayingPlayTrack(track.getPath()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Ln::d, Logger::LogThrowable);
    }

    @SuppressWarnings("UnusedDeclaration")
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
        return new CursorLoader(getActivity(), LibraryProvider.CONTENT_URI,
                QueueTrackHelper.PROJECTION, null, null, null);
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


        if (!TextUtils.isEmpty(s)) {
            Intent intent = new Intent(getActivity(), QueueResultActivity.class);
            intent.putExtra(QueueResultActivity.QUEUE_FILTER, s.trim());
            getActivity().startActivity(intent);
        }

        mSearchView.onActionViewCollapsed();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
