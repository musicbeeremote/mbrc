package com.kelsos.mbrc.ui.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.PlaylistCursorAdapter;
import com.kelsos.mbrc.data.SyncManager;
import roboguice.fragment.provided.RoboListFragment;
import roboguice.inject.InjectView;

public class PlaylistFragment extends RoboListFragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

  private static final int URL_LOADER = 1921;
  @Inject private SyncManager syncManager;
  @InjectView(android.R.id.list) private ListView mListView;
  private PlaylistCursorAdapter mAdapter;

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.ui_fragment_playlist, container, false);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mAdapter = new PlaylistCursorAdapter(getActivity(), null, 0);
    getLoaderManager().initLoader(URL_LOADER, null, this);
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mListView.setAdapter(mAdapter);
  }

  @Override public void onStart() {
    super.onStart();
    syncManager.startPlaylistSync();
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    //return new CursorLoader(getActivity(), PlaylistHelper.CONTENT_URI,
    //    PlaylistHelper.getProjection(), null, null, null);
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    mAdapter.swapCursor(cursor);
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }
}
