package com.kelsos.mbrc.ui.fragments.browse;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackCursorAdapter;
import com.kelsos.mbrc.dao.Track;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.utilities.Logger;
import roboguice.fragment.RoboListFragment;
import rx.schedulers.Schedulers;

public class BrowseTrackFragment extends RoboListFragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
    PlaylistDialogFragment.OnPlaylistSelectedListener {

  private static final int URL_LOADER = 0x53;
  @Inject private TrackCursorAdapter mAdapter;
  @Inject private RemoteApi api;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getLoaderManager().initLoader(URL_LOADER, null, this);
    this.setListAdapter(mAdapter);
    mAdapter.getPopupObservable()
        .subscribe(this::handlePopupSelection, Logger::logThrowable);
  }

  private void handlePopupSelection(Pair<MenuItem, Track> pair) {
    final MenuItem item = pair.first;
    final Track track = pair.second;

    switch (item.getItemId()) {
      case R.id.popup_track_play:
        queueTracks(track, "now");
        break;
      case R.id.popup_track_playlist:
        break;
      case R.id.popup_track_queue_next:
        queueTracks(track, "next");
        break;
      case R.id.popup_track_queue_last:
        queueTracks(track, "last");
        break;
      default:
        break;
    }
  }

  private void queueTracks(Track track, String action) {
    api.nowplayingQueue("track", action, track.getId())
        .observeOn(Schedulers.io())
        .subscribe((r) -> {
        }, Logger::logThrowable);
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_library, container, false);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    //return new CursorLoader(getActivity(), TrackHelper.CONTENT_URI,
    //    TrackHelper.getProjection(), null, null, null);
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }
}
