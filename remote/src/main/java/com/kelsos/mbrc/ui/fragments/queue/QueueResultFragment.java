package com.kelsos.mbrc.ui.fragments.queue;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.dao.QueueTrackHelper;
import org.jetbrains.annotations.NotNull;
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
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_queue_result, container, false);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurFilter = getArguments().getString(QUEUE_FILTER);
        }

        int[] to = new int[] {
			R.id.trackTitle,
			R.id.trackArtist
        };

		final String[] from = {
			QueueTrackHelper.TITLE,
			QueueTrackHelper.ARTIST
		};

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listitem_track, null, from, to, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(URL_LOADER, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		final String selection = String.format("%s LIKE ? OR %s LIKE ?",
				QueueTrackHelper.ARTIST,
				QueueTrackHelper.TITLE);
		final String like = String.format("%%%s%%", mCurFilter);
		final String[] selectionArgs = {
			like,
			like
		};

        return new CursorLoader(getActivity(), QueueTrackHelper.CONTENT_URI,
				QueueTrackHelper.PROJECTION, selection, selectionArgs, null);

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
