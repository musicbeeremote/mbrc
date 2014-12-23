package com.kelsos.mbrc.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistTrackCursorAdapter;
import com.kelsos.mbrc.ui.fragments.MiniControlFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class PlaylistActivity extends RoboActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int URL_LOADER = 0x721ae;

	@InjectView(android.R.id.list)
	private ListView mList;
	private PlaylistTrackCursorAdapter mAdapter;
	private String mTitle;
	private String mHash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_current_queue);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		Intent intent = getIntent();
		mTitle = intent.getStringExtra("name");
		mHash = intent.getStringExtra("hash");

		registerForContextMenu(mList);
		getFragmentManager().beginTransaction()
				.replace(R.id.playlist_mini_control, MiniControlFragment.newInstance())
				.commit();
	}

	@Override
	public void onStart() {
		super.onStart();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		mAdapter = new PlaylistTrackCursorAdapter(this, cursor, 0);
		mList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}
}
