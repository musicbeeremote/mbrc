package com.kelsos.mbrc.ui.fragments.browse;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.ArtistCursorAdapter;
import com.kelsos.mbrc.dao.Artist;
import com.kelsos.mbrc.dao.ArtistHelper;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.activities.ProfileActivity;
import com.kelsos.mbrc.ui.dialogs.CreateNewPlaylistDialog;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.util.Logger;
import org.jetbrains.annotations.NotNull;
import roboguice.fragment.provided.RoboListFragment;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

public class BrowseArtistFragment extends RoboListFragment
		implements LoaderManager.LoaderCallbacks<Cursor>,
		PlaylistDialogFragment.OnPlaylistSelectedListener,
		CreateNewPlaylistDialog.OnPlaylistNameSelectedListener {

	private static final int URL_LOADER = 0x12;
	@Inject
	private ArtistCursorAdapter mAdapter;
	@Inject
	private RemoteApi api;

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
		return new CursorLoader(getActivity(), ArtistHelper.CONTENT_URI,
				ArtistHelper.PROJECTION, null, null, null);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLoaderManager().initLoader(URL_LOADER, null, this);
		this.setListAdapter(mAdapter);
		AppObservable.bindFragment(this, mAdapter.getPopupObservable())
				.subscribe(this::handlePopupSelection, Logger::LogThrowable);
	}

	private void handlePopupSelection(Pair<MenuItem, Artist> pair) {
		final MenuItem item = pair.first;
		final Artist artist = pair.second;

		switch (item.getItemId()) {
			case R.id.popup_artist_queue_next:
				queueTracks(artist, "next");
				break;
			case R.id.popup_artist_queue_last:
				queueTracks(artist, "last");
				break;
			case R.id.popup_artist_play:
				queueTracks(artist, "now");
				break;
			case R.id.popup_artist_album:
				Intent intent = new Intent(getActivity(), ProfileActivity.class);
				intent.putExtra(ProfileActivity.TYPE, ProfileActivity.ARTIST);
			   	intent.putExtra(ProfileActivity.ID, artist.getId());
				startActivity(intent);
				break;
			case R.id.popup_artist_playlist:
				break;
			default:
				break;
		}

	}

	private void queueTracks(Artist artist, String action) {
		api.nowplayingQueue("artist", action, artist.getId())
				.observeOn(Schedulers.io())
				.subscribe((r) -> {
				}, Logger::LogThrowable);
	}
}
