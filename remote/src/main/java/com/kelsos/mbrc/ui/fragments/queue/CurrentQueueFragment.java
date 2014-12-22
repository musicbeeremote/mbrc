package com.kelsos.mbrc.ui.fragments.queue;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
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
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.CurrentQueueAdapter;
import com.kelsos.mbrc.dao.DaoSession;
import com.kelsos.mbrc.dao.QueueTrackHelper;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.QueueResultActivity;
import com.kelsos.mbrc.util.Logger;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CurrentQueueFragment extends RoboFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int URL_LOADER = 0;
    private CurrentQueueAdapter mQueueAdapter;
    private SearchView mSearchView;

    @Inject
    private SyncManager syncManager;

    @Inject
    private RemoteApi api;

	@Inject
	private DaoSession daoSession;

	@InjectView(R.id.dlv_current_queue)
	private DragSortListView mDslView;
	private DragSortController mController;

	private int dragInitMode = DragSortController.ON_DRAG;
	private boolean removeEnabled = true;
	private int removeMode = DragSortController.FLING_REMOVE;
	private boolean sortEnabled = true;
	private boolean dragEnabled = true;

	public DragSortController getController() {
		return mController;
	}

	public DragSortController buildController(DragSortListView dslv) {
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setRemoveEnabled(removeEnabled);
		controller.setSortEnabled(sortEnabled);
		controller.setDragInitMode(dragInitMode);
		controller.setRemoveMode(removeMode);
		return controller;

	}

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_queue, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mController = this.buildController(mDslView);
		mDslView.setAdapter(mQueueAdapter);
		mDslView.setFloatViewManager(mController);
		mDslView.setOnTouchListener(mController);
		mDslView.setDragEnabled(dragEnabled);

		mDslView.setDropListener((fromIndex, toIndex) -> {
			Ln.d("from: %d to: %d", fromIndex, toIndex);
        });

		mDslView.setRemoveListener(position -> {
			final ContentResolver contentResolver = getActivity().getContentResolver();
			api.nowPlayingRemoveTrack(position)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(resp -> {
						if (resp.isSuccess()) {
							final long id = mQueueAdapter.getItemId(position);
							final Uri uri = Uri.withAppendedPath(QueueTrackHelper.CONTENT_URI,
									Uri.encode(String.valueOf(id)));
							contentResolver.delete(uri, null, null);
						}
					}, Logger::LogThrowable);
        });
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
        return new CursorLoader(getActivity(), QueueTrackHelper.CONTENT_URI,
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
