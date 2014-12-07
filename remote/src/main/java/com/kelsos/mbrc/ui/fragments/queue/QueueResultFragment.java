package com.kelsos.mbrc.ui.fragments.queue;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.kelsos.mbrc.data.db.LibraryProvider;
import com.kelsos.mbrc.data.helpers.QueueTrackHelper;
import roboguice.fragment.provided.RoboListFragment;

public class QueueResultFragment extends RoboListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String QUEUE_FILTER = "QueueFilter";
    private static final int URL_LOADER = 0;
    private SimpleCursorAdapter mAdapter;
    private String mCurFilter;

    public QueueResultFragment() {
    }

    public static QueueResultFragment newInstance(String mCurFilter) {
        QueueResultFragment fragment = new QueueResultFragment();
        Bundle args = new Bundle();
        args.putString(QUEUE_FILTER, mCurFilter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurFilter = getArguments().getString(QUEUE_FILTER);
        }

        int[] to = new int[]{
                R.id.trackTitle,
                R.id.trackArtist
        };

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.ui_list_track_item,
                null, QueueTrackHelper.PROJECTION, to, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                QueueTrackDao.Properties.Id.columnName,
                QueueTrackDao.Properties.Artist.columnName,
                QueueTrackDao.Properties.Title.columnName
        };

        final Uri baseUri = Uri.withAppendedPath(LibraryProvider.CONTENT_FILTER_URI, Uri.encode(mCurFilter));
        return new CursorLoader(getActivity(), baseUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
