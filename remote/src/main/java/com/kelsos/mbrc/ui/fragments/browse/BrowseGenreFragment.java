package com.kelsos.mbrc.ui.fragments.browse;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.GenreCursorAdapter;
import com.kelsos.mbrc.dao.GenreHelper;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboListFragment;

public class BrowseGenreFragment extends RoboListFragment
		implements LoaderManager.LoaderCallbacks<Cursor>,
		PlaylistDialogFragment.OnPlaylistSelectedListener,
		CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

	private static final int URL_LOADER = 1;

	@Inject
	private GenreCursorAdapter mAdapter;

	@Inject
	private RemoteApi api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getLoaderManager().initLoader(URL_LOADER, null, this);
		setListAdapter(mAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_now_playing, menu);
	}

	@Override
	public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_library, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), GenreHelper.CONTENT_URI,
				GenreHelper.PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onPlaylistSelected(String hash) {
	}

	@Override
	public void onNewPlaylistSelected() {
		final CreateNewPlaylistDialog npDialog = new CreateNewPlaylistDialog();
		npDialog.setOnPlaylistNameSelectedListener(this);
		npDialog.show(getFragmentManager(), "npDialog");
	}

	@Override
	public void onPlaylistNameSelected(String name) {

	}
}
