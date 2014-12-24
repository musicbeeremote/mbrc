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
import com.kelsos.mbrc.dao.QueueTrack;
import com.kelsos.mbrc.dao.QueueTrackDao;
import com.kelsos.mbrc.dao.QueueTrackHelper;
import com.kelsos.mbrc.data.DatabaseUtils;
import com.kelsos.mbrc.data.SyncManager;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.QueueResultActivity;
import com.kelsos.mbrc.ui.fragments.MiniControlFragment;
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
	private boolean removeEnabled = false;
	private int removeMode = DragSortController.FLING_REMOVE;
	private boolean sortEnabled = true;
	private boolean dragEnabled = true;

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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.current_queue_sync) {
			syncManager.clearCurrentQueue();
			syncManager.startCurrentQueueSyncing();
		}
		return super.onOptionsItemSelected(item);
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
			final Cursor originCursor = (Cursor) mQueueAdapter.getItem(fromIndex);
			final QueueTrack originTrack = QueueTrackHelper.fromCursor(originCursor);
			final Cursor destinationCursor = (Cursor) mQueueAdapter.getItem(toIndex);
			final QueueTrack destinationTrack = QueueTrackHelper.fromCursor(destinationCursor);

			final Integer originalPosition = originTrack.getPosition();
			final Integer destinationPosition = destinationTrack.getPosition();
			final ContentResolver contentResolver = getActivity().getContentResolver();

			api.nowPlayingMoveTrack(fromIndex, toIndex)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(resp -> {

						if (resp.isSuccess()) {
							final QueueTrackDao queueTrackDao = daoSession.getQueueTrackDao();

							originTrack.setPosition(destinationPosition);
							DatabaseUtils.updatePosition(queueTrackDao.getDatabase(), originalPosition, destinationPosition);
							queueTrackDao.update(originTrack);

							contentResolver.notifyChange(QueueTrackHelper.CONTENT_URI, null);
						}
					}, Logger::LogThrowable);

			Ln.d("from: %d to: %d", fromIndex, toIndex);
        });

		mDslView.setRemoveListener(position -> {

			final ContentResolver contentResolver = getActivity().getContentResolver();
			final Cursor cursor = (Cursor) mQueueAdapter.getItem(position);
			final QueueTrack track = QueueTrackHelper.fromCursor(cursor);

			api.nowPlayingRemoveTrack(track.getPosition())
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(resp -> {
						if (resp.isSuccess()) {
							final Uri uri = Uri.withAppendedPath(QueueTrackHelper.CONTENT_URI,
									Uri.encode(String.valueOf(track.getId())));
							contentResolver.delete(uri, null, null);
						}
					}, Logger::LogThrowable);
        });

		getFragmentManager().beginTransaction()
				.replace(R.id.np_mini_control, MiniControlFragment.newInstance())
				.commit();

		mDslView.setOnItemClickListener((parent, view1, position, id) -> {
			final Cursor cursor = (Cursor) mQueueAdapter.getItem(position);
			final QueueTrack track = QueueTrackHelper.fromCursor(cursor);
			api.nowPlayingPlayTrack(track.getPath())
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(resp -> { }, Logger::LogThrowable);
        });
	}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		final String sortOrder = String.format("%s ASC", QueueTrackHelper.POSITION);
        return new CursorLoader(getActivity(), QueueTrackHelper.CONTENT_URI,
                QueueTrackHelper.PROJECTION, null, null, sortOrder);
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
